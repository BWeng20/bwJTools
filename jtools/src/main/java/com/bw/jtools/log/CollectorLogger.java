/*
 * (c) copyright 2015-2019 Bernd Wengenroth
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.log;

import com.bw.jtools.Log;
import java.util.HashMap;

/**
* Logger back-end that collects messages in memory.<br>
* Purpose is to collect errors and warnings during some operation
* to enable the software to give hints about the reason of failure.
* E.g by providing an detailed error-report about the operation.<br>
* It is used by {@link Log#startCollectMessages(int, boolean) }.
*/
public class CollectorLogger extends Log.LoggerFacade
{
    public HashMap<Long,CollectorThreadLogger > threadLogger = new HashMap<>(10);

    /**
     * Creates a new CollectorLogger.
     */
    public CollectorLogger( )
    {
    }

    protected synchronized void addMessage(int level, CharSequence msg, Throwable t)
    {
        if (this.level >= level )
        {
            CollectorThreadLogger tLogger = threadLogger.get( Thread.currentThread().getId() );
            if ( tLogger != null && tLogger.level >= level )
            {
                final String prefix = Log.LoggerFacade.getLevelPrefix(level);
                tLogger.messages.add( prefix+" "+msg );

                if (t != null && tLogger.maxTraceLines>0)
                    tLogger.messages.add( prefix+" "+Log.getRestrictedStackTrace(t, " ", tLogger.maxTraceLines));
            }
        }

    }


    public synchronized CollectorThreadLogger getThreadLog( )
    {
        return threadLogger.get( Thread.currentThread().getId() );
    }

    public synchronized boolean hasThreadLog( )
    {
        return !threadLogger.isEmpty();
    }


    public synchronized void setThreadLog( CollectorThreadLogger tlog )
    {
        final long tid = Thread.currentThread().getId();
        if ( tlog == null )
            threadLogger.remove( tid );
        else
            threadLogger.put( tid, tlog );

        int maxLevel = Log.NONE;
        for ( CollectorThreadLogger tLogger : threadLogger.values() )
        {
            if (maxLevel < tLogger.level )
                maxLevel = tLogger.level;
        }
        setLevel(maxLevel);
    }

    @Override
    public void error(CharSequence msg)
    {
        addMessage( Log.ERROR, msg, null );
    }

    @Override
    public void warn(CharSequence msg)
    {
        addMessage( Log.WARN, msg, null );
    }

    @Override
    public void info(CharSequence msg)
    {
        addMessage( Log.INFO, msg, null );
    }

    @Override
    public void debug(CharSequence msg)
    {
        addMessage( Log.DEBUG, msg, null );
    }

    @Override
    public void error(CharSequence msg, Throwable t)
    {
        addMessage( Log.ERROR, msg, t );
    }

    @Override
    public void warn(CharSequence msg, Throwable t)
    {
        addMessage( Log.WARN, msg, t );
    }

    @Override
    public void debug(CharSequence msg, Throwable t)
    {
        addMessage( Log.DEBUG, msg, t );
    }

    @Override
    public void info(CharSequence msg, Throwable t)
    {
        addMessage( Log.INFO, msg, t );
    }

}
