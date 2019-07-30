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

import com.bw.jtools.Application;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import com.bw.jtools.Log;


/**
 * Collection of methods to get and set Persistence Settings.<br>
 * Some functionality relies on settings retrieved on static-initialization.<br>
 * The application has to initialize the Store by calling {@link Application#initialize(java.lang.Class) } before
 * accessing any methods from this library.
 */
public class Store
{
    /**
     * The name of the property file used to initialize this library and
     * to provide defaults for properties.<br>
     * Needs to be added to class-path, parallel to the application-class.<br>
     * The value can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.
     * @see #initialize(boolean)
     * @see Application#initialize(java.lang.Class)
     */
    public static String DEFAULT_INI = "defaultsettings.properties";

    /**
     * The key of the property used as application name.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_NAME    = "application.name";

    /**
     * The key of the property used as application name.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_COMPANY = "application.company";

    /**
     * The key of the property used as application icon prefix.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called. If the property is not set, the application name is used as fallback.
     * @see #KEY_APPLICATION_NAME
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_ICON_PREFIX = "application.iconPrefix";

    /**
     * The key of the property used as application name.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.<br>
     * If not set 'prefs.ini' will be used as default.
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_PROPERTIES_FILE = "application.properties";

    /**
     * The key of the property used as application version.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.<br>
     * After {@link #initialize(boolean) initialize()} the value can be accessed by
     * {@link Application#AppVersion Application.AppVersion}.
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_VERSION = "application.version";


    /**
     * Property-key for the URL-prefix for the exception-report-dialog.<br>
     * The value can be added to default-properties or the system-properties by
     * command line.
     * <i>Example value</i>
     */
    public static String PREF_ERROR_REPORT_URL = "error.reportToUrl";

    private static StorageBase storage_;

    /**
     * Property-file to use , retrieved from "defaultsettings.properties" on initialization.<br>
     * Default is "prefs.ini".
     */
    public static String  AppPropertyFile ;


    /**
     * Needs to be called by the application as FIRST method before any
     * other functionality is used from this class.<br>
     * Don't call this method directly.Use {@link com.bw.jtools.Application#initialize(java.lang.Class) Application.initialize(Class)}.
     * @param useSysPropsForDefault if true, the default are initialized from system-properties.
     * @see System#getProperties()
     * @see com.bw.jtools.Application#initialize(java.lang.Class)
     */
    static public void initialize( boolean useSysPropsForDefault)
    {
        Properties defaults = useSysPropsForDefault ? new Properties(System.getProperties()) : new Properties();
        String appName = null;
        String company = null;
        String propFile= null;
        String version = null;
        String appIconPrefix = null;
        boolean forcePropFile = false;
        try
        {
            InputStream is = Application.AppClass.getResourceAsStream(DEFAULT_INI);
            if ( is != null )
            {
                defaults.load(is);
                is.close();
            }

            appName = defaults.getProperty(KEY_APPLICATION_NAME);
            company = defaults.getProperty(KEY_APPLICATION_COMPANY);
            propFile= defaults.getProperty(KEY_APPLICATION_PROPERTIES_FILE);
            version = defaults.getProperty(KEY_APPLICATION_VERSION);
            appIconPrefix = defaults.getProperty(KEY_APPLICATION_ICON_PREFIX);
        }
        catch (IOException ex)
        {
        }

        if ( appName == null || appName .isEmpty() )
        {
            Log.warn("Property '"+KEY_APPLICATION_NAME+"' is not set, please check "+DEFAULT_INI);
            appName = "MyApp";
        }
        if ( company == null || company .isEmpty() ) company = "MyCompany";

        if ( version == null ) version = "1.0";
        if ( propFile== null || propFile.isEmpty() )
        {
            propFile= "prefs.ini";
        }
        else
        {
            forcePropFile = true;
        }

        if ( appIconPrefix != null )
        {
            appIconPrefix = appIconPrefix.trim();
        }
        if ( appIconPrefix == null || appIconPrefix.isEmpty() )
        {
            appIconPrefix = appName;
        }

        Application.AppName         = appName;
        Application.AppVersion      = version;
        Application.AppCompany      = company;
        Application.AppIconPrefix   = appIconPrefix;

        AppPropertyFile = propFile;

        PreferencesStorage.PREF_ROOT_KEY =  company+"/"+appName ;

        // try to be Portable (local ini-file)
        try
        {
            // Get the location of the application jar/class-root
            URL codeLocation = Application.AppClass.getProtectionDomain().getCodeSource().getLocation();
            if (codeLocation != null)
            {
                Path folder = Paths.get( codeLocation.toURI()).toRealPath();
                File fileFolder = folder.toFile();

                if ( fileFolder.exists() && !fileFolder.isDirectory() )
                {
                    // Location may be a jar-file. In this case use containing folder.
                    fileFolder = fileFolder.getParentFile();
                }

                if ( fileFolder.exists() && fileFolder.isDirectory() && fileFolder.canWrite() )
                {
                    Application.setBaseAppDirectory( fileFolder.toPath() );
                }
                else
                {
                    Log.warn("Failed to locate base folder ("+fileFolder+")");
                }
            }
        }
        catch ( Exception se )
        {
            // Possibly no permissions...
        }

        if ( null == Application.getBaseAppDirectory() )
        {
            // Try deduction of app-directory by application class.
            Application.setBaseAppDirectory(null);
        }

        Path propPath = null;

        try
        {
            propPath = Application.getBaseAppDirectory().resolve(AppPropertyFile);
            if ( forcePropFile || Files.exists(propPath) )
            {
                storage_ = new FileStorage( propPath, defaults );
            }
        }
        catch (Exception e)
        {
            Log.debug("Failed to load preferences from "+propPath, e);
        }

        if ( storage_ == null )
        {
            // Be Persistent
            storage_ = new PreferencesStorage(defaults);
        }

    }

    private static void transferStorage( StorageBase newStorage )
    {
        newStorage.clear();
        List<String> keys = storage_.getKeysWithPrefix("");
        for ( String key : keys )
        {
            newStorage.setString(key, storage_.getString(key,null));
        }
        storage_ = newStorage;
    }

    /**
     * Enable or disable storage to java-preferences.<br>
     * If enabled, the current settings are transferred to java-preferences.<br>
     * If disabled, the current settings are transferred to file-storage.
     * The new storage will not automatic be flushed.
     * @param enable If true storage is transferred to java-preferences.
     */
    public static void enablePreferences(boolean enable)
    {
        if ( isPreferencesEnabled() != enable )
        {
            if ( enable )
            {
                transferStorage( new PreferencesStorage( storage_ == null ? null : storage_.getDefaults() ) );
            }
            else
            {
                StorageBase old = storage_;
                transferStorage(
                        new FileStorage(Application.getBaseAppDirectory().resolve(AppPropertyFile),
                        storage_ == null ? null : storage_.getDefaults()));

                old.clear();
            }
        }
    }

    /**
     * Checks if currently java-preferences are enabled.
     * @return true if java-preferences is enabled.
     */
    public static boolean isPreferencesEnabled()
    {
        return storage_ instanceof PreferencesStorage;
    }

    /**
     * Tries to create application base directory if it not exists currently.
     */
    public static void createBaseAppDirectory()
    {
       Path baseAppDataPath = Application.getBaseAppDirectory();
       try
       {
         if ( ! Files.exists( baseAppDataPath ) )
         {
            Files.createDirectory(baseAppDataPath);
         }
       }
       catch ( Exception e )
       {
          Log.error("Failed to create data directory '"+baseAppDataPath.toString()+"'. "+e.getMessage() );

       }
       if ( ! ( Files.isDirectory(baseAppDataPath) && Files.isWritable(baseAppDataPath) ) )
       {
          Log.error("Can't access data directory '"+baseAppDataPath.toString()+"'");
       }
    }

    /**
     * @see StorageBase#getString(java.lang.String)
     * @param key The key of the property to get.
     * @return The value, never null.
     * @throws com.bw.jtools.persistence.MissingPropertyException Thrown if property is not set.
     */
    public static String getString(String key) throws MissingPropertyException
    {
        return storage_.getString(key);
    }

    /**
     * @see StorageBase#getString(java.lang.String, java.lang.String)
     * @param key The key of the property to get.
     * @param def Default value in case key is missing.
     * @return The retrieved value or given default.
     */
    public static String getString(String key, String def)
    {
        return storage_.getString(key, def);
    }

    /**
     * @see StorageBase#getDouble(java.lang.String, double)
     * @param key The key of the property to get.
     * @param def Default value in case key is missing.
     * @return The retrieved value or given default.
     */
    public static double getDouble(String key, double def)
    {
      return storage_.getDouble(key, def);
    }

    /**
     * @see StorageBase#setDouble(java.lang.String, double)
     * @param key The key of the property to set.
     * @param value The new value to set.
     */
    public static void setDouble(String key, double value)
    {
        storage_.setDouble(key, value);
    }

    /**
     * @see StorageBase#getInt(java.lang.String, int)
     * @param key The key of the property to get.
     * @param def Default value in case key is missing.
     * @return The retrieved value or given default.
     */
    public static int getInt(String key, int def)
    {
      return storage_.getInt(key, def);
    }

    /**
     * @see StorageBase#setInt(java.lang.String, int)
     * @param key The key of the property to set.
     * @param value The new value to set.
     */
    public static void setInt(String key, int value)
    {
        storage_.setInt(key, value);
    }

    /**
     * @see StorageBase#getBoolean(java.lang.String, boolean)
     * @param key The key of the property to get.
     * @param def Default value in case key is missing.
     * @return The retrieved value or given default.
     */
    public static boolean getBoolean(String key, boolean def)
    {
        return storage_.getBoolean(key, def);
    }

    /**
     * @see StorageBase#setBoolean(java.lang.String, boolean)
     * @param key The key of the property to set.
     * @param value The new value to set.
     */
    public static void setBoolean(String key, boolean value)
    {
        storage_.setBoolean(key, value);
    }


    /**
     * @see StorageBase#setString(java.lang.String, java.lang.String)
     * @param key The key of the property to set.
     * @param value The new value to set.
     */
    public static void setString(String key, String value)
    {
        storage_.setString(key, value);
    }

    /**
     * @see StorageBase#getKeysWithPrefix(java.lang.String)
     * @param prefix The prefix for which all keys shall be retrieved.
     * @return The found list of keys.
     */
    public static List<String> getKeysWithPrefix(String prefix)
    {
       return storage_.getKeysWithPrefix(prefix);
    }

    /**
     * Sets a preference from an other if not set already, Can be used to
     * take over other preferences as defaults.
     * @param keyFrom Source key to copy from.
     * @param keyTo Target key to copy to.
     */
    public static void copyIfNotSet(String keyFrom, String keyTo)
    {
        if (Store.getString(keyTo, null) == null)
        {
            String pref = Store.getString(keyFrom, null);
            if (pref != null)
                Store.setString(keyTo, pref);
        }
    }

    /**
     * Deletes a property.
     * @param key The key to delete.
     */
    public static void deleteKey(String key)
    {
       storage_.deleteKey(key);
    }

    /**
     * Flush all changes to storage-backend.
     */
    public static void flushStorage()
    {
       storage_.flush();
    }

}
