/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.io;

import com.bw.jtools.Log;
import com.bw.jtools.ui.UITool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A file listener that behave similar to the "tail" command.
 */
public final class Tail
{
    private static Thread pollThread = null;
    private static final List<StreamInfo> streams = new ArrayList<>();

    /**
     * Interface for listeners.
     */
    public static interface TailListener
    {
        /**
         * Called if new data is available.
         * @param data the received data.
         * @param offset Offset into the array.
         * @param size Amount of data.
         */
        public void read( byte[] data, int offset, int size );

        /**
         * Called if a exception was received during read.
         * @param ex The thrown exception.
         */
        public void exception( Throwable ex );
    }

    static private class StreamInfo {
        InputStream stream;
        List<TailListener> listener = new ArrayList<>();
        byte[] buffer = new byte[10240];
        int min;
        int max;
        int start;
        int end;

        StreamInfo( InputStream stream, int min, int max )
        {
            this.stream = stream;
            this.min = (min<100)?100:(min>512)?512:min;
            this.max = (max<512)?512:max;
            this.start = 0;
            this.end = 0;
        }

        void adaptLimits( int min, int max )
        {
            min = (min<100)?100:(min>512)?512:min;
            max = (max<512)?512:max;

            if ( min > max ) min = max;

            synchronized( this )
            {
                if ( min < this.min )
                {
                    int avail = getAvail();
                    if ( avail >= min )
                    {
                        this.end = start+min;
                        fireData(this);
                        this.end += avail-min;
                    }
                    this.min = min;
                }
                this.max = max;
            }
        }

        int getOffset()
        {
            return start;
        }

        int getAvail()
        {
            return end-start;
        }

        void consume( int amount )
        {
            synchronized(this) {
                start += amount;
                if ( start >= end )
                {
                    start = 0;
                    end = 0;
                }
            }
        }

    }

    static void fireData( final StreamInfo s )
    {
        UITool.executeInUIThread( () -> {
            synchronized ( s )
            {
                while( s.getAvail() >= s.min )
                {
                    final int amount = Math.min( s.max, s.getAvail());
                    for ( TailListener l : s.listener )
                    {
                        try
                        {
                            l.read(s.buffer, s.getOffset(), amount );
                        }
                        catch ( Exception e)
                        {
                            Log.error("TailListener failed: "+e.getMessage(), e);
                        }
                    }
                    s.consume( amount );
                }
            }
        });
    }

    static void fireException( StreamInfo s, Throwable payload )
    {
        UITool.executeInUIThread( () -> {
            synchronized ( s )
            {
                for ( TailListener l : s.listener )
                {
                    l.exception(payload);
                }
            }
        });
    }


    /**
     * Adds a stream to monitor.
     * @param inputstream The stream. Must not be null.
     * @param t The listener. Must not be null.
     * @param minLimit Minimal amount of bytes to notify.
     * @param maxLimit Maximal amount of bytes to notify.
     */
    static public void addStream( InputStream inputstream, TailListener t, int minLimit, int maxLimit )
    {
        /* As Channel-Selectors are currently not available for Files: We have to poll...*/

        Objects.requireNonNull(inputstream, "InputStream must not be null");
        Objects.requireNonNull(t, "TailListener must not be null");

        StreamInfo s = null;
        synchronized ( streams )
        {
            for ( StreamInfo si : streams ) {
                if (si.stream == inputstream )
                {
                    s = si;
                    break;
                }
            }
            if ( s == null )
            {
                final StreamInfo ns = new StreamInfo( inputstream, minLimit, maxLimit );
                ns.listener.add ( t);
                streams.add( ns );
            }
        }
        if ( s != null )
        {
            // Don't sync on a StreamInfo inside sync on "streams".
            // The "fire"-Methods above sync the StreamInfo and call listener, which can
            // call one of the add/remove-methods. This would lead to a dead-lock.
            synchronized (s )
            {
                s.listener.add(t);
                s.adaptLimits( minLimit, maxLimit );
            }
        }

        synchronized ( Tail.class )
        {
            if ( pollThread == null )
            {
                pollThread = new Thread(() ->
                {
                    boolean run = true;
                    do
                    {
                        try {
                            Thread.sleep(1000);
                        } catch ( Exception e) {}

                        List<StreamInfo> localstreams;
                        synchronized ( streams )
                        {
                            if ( streams.isEmpty() )
                            {
                                run = false;
                            }
                            else
                            {
                                localstreams = new ArrayList<>(streams.size());
                                localstreams.addAll(streams);
                            }
                        }

                        if ( run )
                        {
                            for ( StreamInfo is : streams )
                            {
                                try {
                                    int av = is.stream.available();
                                    if ( (is.getAvail()+av) >= is.min )
                                    {
                                        int amount = Math.min( av, is.buffer.length-is.end );
                                        if ( amount >= is.min || is.getAvail()<is.min )
                                        {
                                            is.stream.read(is.buffer, is.end, amount );
                                            is.end += amount;
                                            fireData( is );
                                        }
                                    }
                                }
                                catch ( Exception e)
                                {
                                    fireException( is, e );
                                }
                            }
                        }
                        else
                        {
                            synchronized(streams ) {
                                run = !streams.isEmpty();
                                if ( !run  ){
                                    synchronized( Tail.class )
                                    {
                                        pollThread = null;
                                    }
                                }
                            }
                        }

                    } while (run);
                });
                pollThread.start();
            }
        }
    }

    /**
     * Removed a stream from monitor.<br>
     * If both arguments are null, all stream are removed from monitor.
     * @param inputstream The stream. if null, all streams for the listener are removed.
     * @param t The listener. If null, all listener are removed.
     */
    static public void removeStream( InputStream inputstream, TailListener t )
    {
        synchronized ( streams )
        {
            for ( Iterator <StreamInfo> sit = streams.iterator(); sit.hasNext(); )
            {
                StreamInfo si = sit.next();
                if ( (inputstream == null || si.stream == inputstream) &&
                     (t == null || si.listener.contains(t) ) )
                 {
                    if ( t != null ) {
                        si.listener.remove(t);
                    }
                    else {
                        si.listener.clear();
                    }
                    if ( si.listener.isEmpty() )
                    {
                        sit.remove();
                    }
                }
            }
        }
    }

    /**
     * Stop processing.
     */
    static public void stop()
    {
        synchronized ( streams )
        {
            for ( StreamInfo si : streams )
            {
                try
                {
                    si.stream.close();
                    si.listener.clear();
                }
                catch ( Exception e)
                {}
            }
            streams.clear();
        }
        Thread t = pollThread;
        if ( t != null )
        {
            try
            {
                t.join();
            }
            catch ( Exception e)
            {}
        }
    }

}
