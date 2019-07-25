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
package com.bw.jtools.persistence;

import com.bw.jtools.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A IStorage implementation that stores all values in one property-file.
 */
class FileStorage implements IStorage
{
    private final Path path_;
    private final HashMap<String,String> values_ = new HashMap<>();
    private boolean dirty_ = false;
    private boolean loaded_ = false;

    /**
     * Creates a storage with file to use.
     * @param storageFile The file to use.
     */
    FileStorage( Path storageFile )
    {
        path_ = storageFile;
    }

    private void loadIfNeeded()
    {
        if ( loaded_ == false )
        {
            loaded_ = true;
            try
            {
                try (BufferedReader reader = Files.newBufferedReader(path_))
                {
                    reader.lines().forEach((line) ->
                    {
                        line = line.trim();
                        if ( !line.startsWith("#") )
                        {
                            final int pos = line.indexOf('=');
                            if ( pos > 0 )
                            {
                                final String key = line.substring(0,pos).trim();
                                if ( !values_.containsKey(key) )
                                {
                                    values_.put( key, line.substring(pos+1).trim() );
                                }
                            }
                        }
                    });
                }
            }
            catch ( Exception e)
            {
                Log.error("Failed to read settings: "+e.getMessage());
            }
        }
    }


    @Override
    public synchronized String getString(String key)
    {
        loadIfNeeded();
        return values_.get(key);
    }

    @Override
    public synchronized void setString(String key, String value)
    {
        if ( values_.containsKey(key))
        {
            String oldVal = values_.get(key);
            if ( value == null ? (oldVal==null) : value.equals(oldVal) )
            {
                return;
            }
        }
        values_.put(key, value);
        dirty_ = true;
    }

    @Override
    public synchronized void deleteKey(String key)
    {
        if ( loaded_ )
        {
            if ( values_.containsKey(key))
            {
                if ( values_.get(key) != null )
                {
                    dirty_ = true;
                    values_.put(key, null);
                }
            }
        }
        else
        {
            dirty_ = true;
            values_.put(key, null);
        }
    }

    @Override
    public synchronized void clear()
    {
        dirty_ = false;
        values_.clear();
        try
        {
            if ( Files.exists(path_) )
                Files.delete(path_);
        }
        catch ( Exception e)
        {
            Log.error(e.getMessage());
        }
    }


    @Override
    public synchronized void flush()
    {
        if ( dirty_ )
        {
            loadIfNeeded();
            try
            {
                // For convenience of humans sort the keys.
                ArrayList<String> keys = new ArrayList<>(values_.size());
                keys.addAll( values_.keySet() );
                java.util.Collections.sort(keys);

                try (BufferedWriter writer = Files.newBufferedWriter(path_,StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE ))
                {
                    writer.write("# "+Store.AppName+" Settings\n");
                    for ( String k : keys )
                    {
                        final String value = values_.get(k);
                        if ( value != null )
                        {
                            writer.write(k);
                            writer.write("=");
                            writer.write(value);
                            writer.write("\n");
                        }
                    }
                }
                dirty_ = false;
            }
            catch ( Exception e )
            {
                 Log.error(e.getMessage());
            }
        }
    }

    @Override
    public synchronized List<String> getKeysWithPrefix(String prefix)
    {
        loadIfNeeded();

        ArrayList<String> l = new ArrayList<>();
        for ( Map.Entry<String,String> entry : values_.entrySet() )
        {
            if ( entry.getValue() != null && entry.getKey().startsWith(prefix) )
            {
                l.add(entry.getKey());
            }
        }
        return l;
    }

}
