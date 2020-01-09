/*
 * (c) copyright 2015-2019 Bernd Wengenroth
 *
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
package com.bw.jtools.io;

import com.bw.jtools.Log;
import com.bw.jtools.persistence.Store;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Light weight file monitoring.<br>
 * Supports java.nio.file.WatchService or Poll-mode.<br>
 *
 * WatchService works well for local storage on e.g. windows and possibly for linux (watch-out for bugs!).<br>
 * Watching a network-device may work under some conditions under windows, but is impossible for linux, as the
 * inotify-API used for that supports only local storage.
 */
public class DirectoryMonitor
{
    protected FileSystem fileSystem;

    protected final ConcurrentHashMap< String, PathData> paths = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap< WatchKey, PathData> watchServiceKeys = new ConcurrentHashMap<>();

    public static final class ChangeData
    {
        public final Path path;
        public final ChangeMode kind;

        private ChangeData(Path path, ChangeMode kind)
        {
            this.path = path;
            this.kind = kind;
        }
    };

    /**
     * Enum to indicate the type of a file-system change.
     */
    public static enum ChangeMode
    {
        CREATED,
        REMOVED,
        CHANGED,
        INITIAL
    };


    protected static final class PathData
    {
        public Vector<ChangeData> changes = new Vector<>(10);

        public boolean error_state = false;
        public boolean poll = false;
        private boolean handleInitialFiles = false;
        private boolean firstScan = true;

        private final Path path;
        private HashMap<String,Long> poll_CurrentState = new HashMap<>();
        private HashMap<String,Long> poll_next = new HashMap<>();
        private final List<DirectoryMonitorListener> listener = new ArrayList<>();

        public PathData( Path path, boolean handleInitialFoundFiles )
        {
            this.path = path;
            this.handleInitialFiles = handleInitialFoundFiles;
        }

        public void pollChanges()
        {
            HashMap<String,Long> tmp;

            try
            {
                long lmods[];

                File files[] = path.toFile().listFiles();
                if ( files != null )
                {
                    lmods = new long[files.length];

                    for ( int i=0 ; i<files.length ; ++i )
                    {
                        lmods[i] = files[i].lastModified();
                    }
                }
                else
                {
                    files = new File[0];
                    lmods = new long[0];
                }

                synchronized ( this )
                {
                    poll_next.clear();
                    for ( int i=0 ; i<files.length ; ++i )
                    {
                        final File f = files[i];
                        final long lmod = lmods[i];

                        final String name = f.getName();
                        if ( lmod != 0L )
                        {
                            Long lastMod = poll_CurrentState.remove( name );
                            poll_next.put( name, lmod );

                            if ( lastMod != null )
                            {
                                if ( lastMod.longValue() != lmod )
                                {
                                    changes.add(new ChangeData(path.resolve(name),
                                            ChangeMode.CHANGED));
                                }
                            }
                            else
                            {
                                changes.add(new ChangeData(path.resolve(name), firstScan ? ChangeMode.INITIAL : ChangeMode.CREATED));
                            }
                        }
                    }
                    for ( String name : poll_CurrentState.keySet() )
                    {
                        changes.add(new ChangeData(path.resolve(name),ChangeMode.REMOVED));
                    }
                    tmp = poll_CurrentState;
                    poll_CurrentState = poll_next;
                    poll_next = tmp;
                }

                handleInitialFiles = false;
                firstScan = false;
                error_state = false;
            }
            catch ( Exception e )
            {
                if ( !error_state )
                {
                    error_state = true;
                    Log.error(path.toFile().getPath()+": "+e.getMessage(), e);
                }
            }
        }

        protected void notifyListener(DirectoryMonitor dm)
        {
            Vector<ChangeData> changes_switch;
            synchronized( this )
            {
                changes_switch = changes;
                changes = new Vector<>();
            }

            for ( ChangeData cd : changes_switch )
            {
                for ( DirectoryMonitorListener l : listener )
                {
                    if ( cd.kind == ChangeMode.CHANGED )
                    {
                        l.fileChanged(dm, cd.path );
                    }
                    else if ( cd.kind == ChangeMode.CREATED )
                    {
                        l.fileAdded( dm, cd.path );
                    }
                    else if ( cd.kind == ChangeMode.REMOVED )
                    {
                        l.fileRemoved(dm, cd.path );
                    }
                    else if ( cd.kind == ChangeMode.INITIAL )
                    {
                        l.fileInitialAdded(dm, cd.path );
                    }
                }
            }
            changes_switch.clear();
        }
    }


    /**
     * Property-key for poll time.
     */
    public static final String KEY_POLL_TIME_MS = "filemonitor.pollTimeMS";

    /**
     * Property-key-prefix for path that needs polling.
     */
    public static final String KEY_POLL_PATH_PREFIX = "filemonitor.pathToPoll.";

    private static final ArrayList<String> pathsToPoll = new ArrayList<>();
    private static boolean pathsToPoll_Read = false;
    private static int pollTimeOutMS = 1000;

    protected Thread watchThread = null;
    protected Thread pollThread = null;
    protected WatchService watchService = null;

    /**
     * Check if the path needs manual polling. Root paths that can't be handled by the
     * WatchService needs to be configured with a configuration entry with prefix
     * "filemonitor.pathToPoll.INDEX".
     * @param path_searched The path tha should be checked.
     * @return  True if the paths should be polled.
     */
    public static boolean needsPolling( Path path_searched )
    {
        if ( !pathsToPoll_Read )
        {
            synchronized ( pathsToPoll )
            {
                // Check again in protected code to ensure we set this only once
                // (access to object-variables itself is always thread-safe)
                if ( !pathsToPoll_Read )
                {
                    pollTimeOutMS = Store.getInt(KEY_POLL_TIME_MS, 1000);
                    List<String> keys = Store.getKeysWithPrefix(KEY_POLL_PATH_PREFIX);
                    pathsToPoll.clear();
                    pathsToPoll.ensureCapacity(keys.size());
                    for ( String pkey : keys )
                    {
                        String pval = Store.getString(pkey, null);
                        if ( pval != null && !pval.isEmpty())
                        {
                            try
                            {
                                // Try to canonicallize.
                                pval = new File( pval ).getCanonicalPath();
                            }
                            catch (Exception ex)
                            {
                                Log.warn( pkey+"="+pval+": failed to create canonical path. Using un-checked specified value.", ex );
                            }
                            pathsToPoll.add( pval );
                        }
                    }
                    pathsToPoll_Read = true;
                }
            }
        }

        final String path_name = path_searched.toFile().getPath();
        for ( String p : pathsToPoll)
        {
            if ( path_name.startsWith(p) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new Monitor.
     */
    public DirectoryMonitor( )
    {

    }

    /**
     * Stops the monitor.
     * Not yet implemented
     */
    public void stop()
    {
        //@TODO finally implement this!
    }

    /**
     * Get PathData instance for this path.<br>
     * The canonical version of the path is used
     * as key to retrieve the stored PathData.
     * @param path The path.
     * @return PathData for the path.
     */
    public PathData getPathData( Path path )
    {
        try
        {
            File file = path.toFile();
            // Try to canonicalize.
            synchronized ( this.paths )
            {
                return paths.get(file.getCanonicalFile().getPath());
            }
        }
        catch (Exception ex)
        {
            return null;
        }

    }

    protected PathData addPath( Path path, boolean handleInitialFoundFiles ) throws IOException
    {
        File file = path.toFile();
        if ( !file.isDirectory())
        {
            throw new IOException("DirectoryMonitor:'"+path+"' is not a directoy.");
        }

        try
        {
            // Try to canonicallize.
            file = file.getCanonicalFile();
            path = file.toPath();
        }
        catch (Exception ex)
        {
            Log.warn( "DirectoryMonitor:'"+path+"' - failed to create canonical path. Using un-checked specified value.", ex );
        }

        boolean poll = needsPolling(path);

        synchronized ( this.paths )
        {
            PathData pd = new PathData(path, handleInitialFoundFiles );
            pd.poll = poll;
            this.paths.put( file.getPath(), pd );

            if ( poll || handleInitialFoundFiles )
            {
                if ( poll )
                    Log.info("DirectoryMonitor: Polling for "+path );

                if ( pollThread == null )
                {
                    pollThread = new Thread(() ->
                    {
                        pollForChanges();
                    }, "DmPoll");
                    pollThread.start();

                }

            }
            if ( !poll )
            {
                final FileSystem fs = path.getFileSystem();
                if ( fileSystem == null )
                {
                    fileSystem = fs;
                    watchService = fileSystem.newWatchService();
                }
                else if ( fs != fileSystem)
                {
                    Log.warn("DirectoryMonitor:"+path+" - Try to handle different file-systems by one instance, this may fail.");
                }
                WatchKey key =
                        path.register( watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY );

                Log.info("DirectoryMonitor: Watching "+path );

                watchServiceKeys.put( key, pd );

                if ( watchThread == null )
                {
                    watchThread = new Thread(() ->
                    {
                        handleWatchServiceEvents();
                    }, "DmWatch");
                    watchThread.start();
                }
            }
            return  pd;
        }
    }

    /**
     * Adds a path to monitor.
     *
     * @param path The path to monitor.
     * @param handleInitialFoundFiles Report also initial found files.
     * @param l The listener that will be called if changes are detected.
     * @return The normalized path that will be used to monitor.
     * @throws java.io.IOException Thrown if path could not be accessed or is no directroy at all.
     */
    public synchronized Path addPath( Path path, boolean handleInitialFoundFiles, DirectoryMonitorListener l ) throws IOException
    {
        PathData pd = getPathData( path );
        if ( pd == null )
            pd = addPath(path, handleInitialFoundFiles);

        if ( pd != null )
        {
            pd.listener.remove(l);
            if (l != null )
                pd.listener.add(l);
            return pd.path;
        }
        return null;

    }

    /**
     * Remove the path from monitor.<br>
     * A path is finally removed only if all on this path registered listeners
     * are removed.
     * @param path The path to remove.
     * @param listener The listener that registered the path.
     */
    public synchronized void removePath( Path path, DirectoryMonitorListener listener )
    {
        File file = path.toFile();
        // Try to canonicalize.
        synchronized ( this.paths )
        {
            try
            {
                final String cannonialPath = file.getCanonicalFile().getPath();
                PathData pd = paths.get(cannonialPath);
                if ( pd != null )
                {
                    pd.listener.remove(listener);
                    if (pd.listener.isEmpty())
                    {
                        this.paths.remove(cannonialPath);
                        if ( pd.poll )
                        {
                        }
                    }
                
                }
            } 
            catch ( Exception ex )
            {
            }            
        }
    }


    protected void pollForChanges()
    {
        ArrayList<PathData> path2Poll = new ArrayList<>();
        for (;;) {

            try
            {
                Thread.sleep(pollTimeOutMS);
            }
            catch (InterruptedException ex)
            {
            }

            synchronized ( paths )
            {
                for ( PathData pd : paths.values())
                {
                    if ( pd.poll || pd.handleInitialFiles )
                    {
                        path2Poll.add(pd);
                    }
                }
            }
            for ( PathData pd : path2Poll)
            {
                pd.pollChanges();
                pd.notifyListener(this);
            }
            path2Poll.clear();
        }
    }

    protected void handleWatchServiceEvents()
    {
        for (;;) {

            WatchKey key;
            boolean overflowOccured = false;

            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                Log.error("DirectoryMonitor: WatchService interrupted.");
                return;
            }

            PathData pd = watchServiceKeys.get(key);
            if ( pd == null )
            {
                Log.warn("DirectoryMonitor: Unknown key reported: "+key.watchable() );
                key.cancel();
            }
            else
            {
                List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event: events )
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                    {
                        overflowOccured = true;
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
                    if ( filename != null )
                    {
                        try
                        {
                            synchronized( this )
                            {
                                final String name = filename.toFile().getName();
                                final Path path = pd.path.resolve(filename);
                                final File file = path.toFile();

                                if ( file.exists() )
                                {
                                    Long lastMod = pd.poll_CurrentState.get( name );

                                    long newMod = file.lastModified();
                                    if ( lastMod == null || lastMod != newMod )
                                    {
                                        synchronized ( pd  )
                                        {
                                            pd.poll_CurrentState.put( name, newMod );
                                            if  ( kind == StandardWatchEventKinds.ENTRY_CREATE)
                                            {
                                                pd.changes.add( new ChangeData(path, ChangeMode.CREATED ) );
                                            }
                                            else if  ( kind == StandardWatchEventKinds.ENTRY_DELETE)
                                            {
                                                pd.changes.add( new ChangeData(path, ChangeMode.REMOVED ) );
                                            }
                                            else if  ( kind == StandardWatchEventKinds.ENTRY_MODIFY)
                                            {
                                                pd.changes.add( new ChangeData(path, ChangeMode.CHANGED ) );
                                            }
                                        }
                                    }
                                    else
                                        continue;
                                }
                                else
                                {
                                    synchronized ( pd  )
                                    {
                                        pd.poll_CurrentState.remove( name );
                                        if ( kind == StandardWatchEventKinds.ENTRY_DELETE )
                                        {
                                            pd.changes.add( new ChangeData(path, ChangeMode.REMOVED) );
                                        }
                                        else
                                            continue;
                                    }
                                }
                            }
                            pd.notifyListener(this);
                        }
                        catch ( Exception e)
                        {

                        }
                    }
                }
            }

            if (!key.reset())
            {
                Log.error("DirectoryMonitor: Directory lost: "+pd.path.toString());
                key.cancel();
                break;
            }
            if ( overflowOccured )
            {
                Log.error("DirectoryMonitor: Event Overflow for "+pd.path.toString());
            }

        }
    }

}
