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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Abstract base for storage back-ends.
 */
public abstract class StorageBase
{
    protected StorageBase( Properties defaults )
    {
        this.defaults_ = defaults == null ? new Properties() : defaults;
    }

    /**
     * Set handling of empty values.<br>
     * @param  emptyAsNull if true all empty values will be handled as null.
     */
    public void setHandleEmptyAsNull( boolean emptyAsNull )
    {
        handleEmptyAsNull_ = emptyAsNull;
    }


    /**
     * Get the currently used defaults.
     * @return The defaults, never null.
     */
    public Properties getDefaults()
    {
        return defaults_;
    }

    /**
     * Get a string-value from storage.<br>
     * If the value is missing in the storage-back-end, defaults are checked.<br>
     * If storage-back-end and defaults have no value, <i>defaultVal</i> is returned.<br>
     * The default value will not be checked for emptiness.
     * As conclusion <i>getString("unknownkey", "" )</i> will return the empty default-value even if
     * "HandleEmptyAsNull" is on.
     * @param key The preference-key.
     * @param defaultVal The default value. Will not be checked for emptiness.
     * @return The stored value or null.
     */
    public final String getString(String key, String defaultVal )
    {
        String v = getString_impl(key);
        if ( v != null && handleEmptyAsNull_ && v.isEmpty() ) v = null;
        if ( v == null )
        {
            v = (String)defaults_.getProperty(key);
        }
        if ( v == null ) return defaultVal;
        return v;
    }

    /**
     * Get a string-value from storage.<br>
     * If the value is missing in the storage-back-end, defaults are checked.<br>
     * If storage-back-end and defaults have no value, an exception is thrown.<br>
     * If missing values shouldn't result in exceptions, use {@link #getString(java.lang.String, java.lang.String) getString(key,null)}.<br>
     * This method is useful where a number of mandatory settings needs to be retrieved.
     * In this case it is much easier to encapsulate all calls in one exception block instead to check each value.
     * @param key The preference-key.
     * @return The stored value - never null.
     * @throws com.bw.jtools.persistence.MissingPropertyException Thrown in case the key was not found.
     */
    public final String getString(String key ) throws MissingPropertyException
    {
        String v = getString(key,null);
        if ( v == null ) throw new MissingPropertyException(key);
        return v;
    }

    /**
     * Get a property and convert it to a double value.
     * @param key The preference key.
     * @param def The default value.
     * @return The retrieved value or the default.
     */
    public final double getDouble(String key, double def)
    {
      try
      {
          final String val = getString(key, String.valueOf(def) );
          return Double.parseDouble(val);
      }
      catch ( Exception e)
      {
          Log.warn("Failed to parse floating-point property "+key+".");
      }
      return def;
    }

    /**
     * Get a property and convert it to a double value.
     * @param key The preference key.
     * @return The retrieved value or the default.
     * @throws MissingPropertyException In case the value for this key is missing or couldn't be converted.
     */
    public final double getDouble(String key) throws MissingPropertyException
    {
        final String val = getString(key);
        try
        {
            return Double.parseDouble(val);
        }
        catch ( Exception e)
        {
            Log.error("Failed to parse floating-point property "+key+".");
            throw new MissingPropertyException(key,e);
        }
    }


    /**
     * Sets a property.
     * @param key The preference key.
     * @param value The new value.
     */
    public final void setDouble(String key, double value)
    {
        setString(key, String.valueOf(value));
    }

    /**
     * Get a property and convert it to an integer value.
     * @param key The preference key.
     * @return The retrieved value.
     * @throws MissingPropertyException In case the value for this key is missing or couldn't be converted.
     */
    public final int getInt(String key) throws MissingPropertyException
    {
        final String val = getString(key );
        try
        {
            return Integer.parseInt(val);
        }
        catch ( Exception e)
        {
            Log.error("Failed to parse integer property "+key+".");
            throw new MissingPropertyException(key,e);
        }
    }

    /**
     * Get a property and convert it to an integer value.
     * @param key The preference key.
     * @param def The default value.
     * @return The retrieved value or the default.
     */
    public final int getInt(String key, int def)
    {
      try
      {
          final String val = getString(key, String.valueOf(def) );
          return Integer.parseInt(val);
      }
      catch ( Exception e)
      {
          Log.warn("Failed to parse integer property "+key+".");
      }
      return def;
    }

    /**
     * Sets a property.
     * @param key The preference key.
     * @param value The new value.
     */
    public final void setInt(String key, int value)
    {
        setString(key, String.valueOf(value));
    }

    /**
     * Get a property, interpreting it as boolean.<br>
     * If the property is "on", "yes" or "true", the method returns true.<br>
     * This check is done none-case-sensitive.
     * @param key The preference key.
     * @param def The default value.
     * @return The retrieved value or the default.
     */
    public final boolean getBoolean(String key, boolean def)
    {
        final String val = getString(key, def ? "On":"Off" );
        return  val.equalsIgnoreCase("On"  ) ||
                val.equalsIgnoreCase("Yes" )||
                val.equalsIgnoreCase("True") ;
    }

    /**
     * Sets a property.<br>
     * For true "On" is set, for false "Off".
     * @param key The preference key.
     * @param value The new value.
     */
    public final void setBoolean(String key, boolean value)
    {
        setString( key, value ? "On" : "Off" );
    }

    /**
     * Needs to be implemented by back-end.
     * @param key They key of the property.
     * @return  The value or null.
     */
    protected abstract String getString_impl(String key);

    /**
     * Set a string-value in storage.
     * @param key The preference-key.
     * @param value The value.
     */
    public abstract void setString(String key, String value);

    /**
     * Deleted a value from storage.
     * @param key The preference-key.
     */
    public abstract void deleteKey(String key);

    /**
     * Makes all changes persistent.
     */
    public abstract void flush();

    /**
     * Removes all keys and values.
     */
    public abstract void clear();

    /**
     * Gets a list with all keys that share some prefix.<br>
     * Defaults are not considered here.
     * @param prefix The common prefix.
     * @return The list of matching preferences.
     */
    public Collection<String> getKeysWithPrefix(String prefix) {
        // Implementations can also override this method if optimization is needed.

        ArrayList<String> l = new ArrayList<>();
        Collection<String> allKeys = getAllKeys();
        for ( String key : allKeys )
        {
            if ( key != null && getString_impl(key) != null && key.startsWith(prefix) )
            {
                l.add(key);
            }
        }
        return l;

    }

    /**
     * Needs to be implemented by back-end.
     * @return All keys. May also contain null values.
     */
    protected abstract Collection<String> getAllKeys();

    protected Properties defaults_;
    private boolean handleEmptyAsNull_ = false;


}