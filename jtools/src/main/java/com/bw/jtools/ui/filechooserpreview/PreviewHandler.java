/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.ui.filechooserpreview;

import com.bw.jtools.Log;

import java.io.File;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * Base class for Preview-handlers.
 */
public abstract class PreviewHandler
{
    /**
     * Global  cache with weak references to release the data if gc need memory.
     */
    protected static final WeakHashMap<String, PreviewProxy> cache_ = new WeakHashMap<>();

    protected PreviewConfig config_;

    /**
     * File name pattern for supported file types.
     */
    protected final Pattern fileNamePattern_;

    /**
     * To be called by implementations.
     * @param pattern The Regular Expression to be used for detections of matching file-types.
     *                Example: "(?i).*\\.(xml|html|htm)"
     */
    protected PreviewHandler( String pattern )
    {
        fileNamePattern_ = Pattern.compile(pattern);
    }

    /**
     * Get the preview proxy for the file if this handlers can hande the type of file.
     * @param file The file to handle.
     * @param canonicalPath The unique path - can be used for internal identification.
     * @return The proxy or null if the file is not handled by this instance.
     */
    public PreviewProxy getPreviewProxy( File file, String canonicalPath )
    {
        PreviewProxy proxy;
        try
        {
            if ( config_ == null || !fileNamePattern_.matcher(canonicalPath).matches())
                return null;

            final String cachekey = getCacheKey(file, canonicalPath);
            synchronized (cache_)
            {
                long lastMod = file.lastModified();
                proxy = cache_.get(cachekey);

                if (proxy != null && proxy.lastMod_ < lastMod)
                {
                    Log.debug("File was modified ("+proxy.lastMod_+" < "+lastMod+") Proxy "+proxy);
                    // Needs reload
                    proxy.dispose();
                    proxy = null;
                }

                if (proxy == null && file.exists())
                {
                    Log.debug("Loading preview " + canonicalPath);
                    proxy = createPreviewProxy( file, canonicalPath );
                    proxy.name_ = file.getName();
                    proxy.path_ = canonicalPath;
                    proxy.lastMod_ = lastMod;
                    proxy.size_ = file.length();
                    cache_.put(cachekey, proxy);
                }

                synchronized (proxy)
                {
                    if ( proxy.complete)
                    {
                        Log.debug("Preview already completed");
                    }
                    else
                    {
                        // Tell proxy to update if finished.
                        proxy.activeAndPending = true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            proxy = null;
        }

        return proxy;
    }

    /**
     * Called by parent to set the configuration.<br>
     * The instance is shared between all handlers. Don't modify it!
     */
    protected void setConfiguration(PreviewConfig config)
    {
        config_ = config;
    }

    /**
     * Create a new proxy. <br>
     * This method doesn't need to care about
     *  {@link PreviewProxy#name_},
     *  {@link PreviewProxy#path_},
     *  {@link PreviewProxy#lastMod_} and
     *  {@link PreviewProxy#size_} as these fields will be set by the caller.
     * @param file The file.
     * @param canonicalPath The canonical path of the file as the preview use to identify the file uniquely.
     */
    protected abstract PreviewProxy createPreviewProxy(File file, String canonicalPath);

    /**
     * Get the global cache key.<br>
     * Needs to be overloaded if the cached pre viewed depand also on other stuff as the file itself.
     * @param file The file.
     * @param canonicalPath The canonical path that is used to identify the file.
     */
    protected String getCacheKey(File file, String canonicalPath)
    {
        return canonicalPath;
    }

}
