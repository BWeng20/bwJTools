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

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * A IStorage implementation that stores all values in as Java Preferences.
 */
class PreferencesStorage implements IStorage
{
    /**
     * The root key.<br>
     * The real root key will be set on initialization from
     * "defaultsettings.properties".
     */
    public static String PREF_ROOT_KEY = "bweng";

    /**
     * Get all keys.
     */
    public static String[] getAllKeys()
    {
       try
       {
          return Preferences.userRoot().node(PREF_ROOT_KEY).keys();
       }
       catch (BackingStoreException ex)
       {
          return new String[0];
       }
    }

    @Override
    public String getString(String key)
    {
        return Preferences.userRoot().node(PREF_ROOT_KEY).get(key, null);
    }

    @Override
    public void setString(String key, String value)
    {
        Preferences.userRoot().node(PREF_ROOT_KEY).put(key, value);
    }

    @Override
    public void deleteKey(String key)
    {
        Preferences.userRoot().node(PREF_ROOT_KEY).remove(key);
    }

    @Override
    public void flush()
    {
       try
       {
         if ( Preferences.userRoot().nodeExists(PREF_ROOT_KEY) )
         {
             Preferences.userRoot().node(PREF_ROOT_KEY).flush();
         }
       }
       catch (BackingStoreException ex)
       {
       }
    }

    @Override
    public synchronized void clear()
    {
       try
       {
         Preferences.userRoot().node(PREF_ROOT_KEY).removeNode();
         Preferences.userRoot().flush();
       }
       catch (BackingStoreException ex)
       {
       }
    }

    @Override
    public List<String> getKeysWithPrefix(String prefix)
    {
       ArrayList<String> l = new ArrayList<>();
       String all[] = getAllKeys();
       for ( String k : all )
       {
          if ( k.startsWith(prefix)) l.add(k);
       }
       return l;
    }
}
