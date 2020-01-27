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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.bw.jtools.Log;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
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
     * The key of the property used as application base directory.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.<br>
     * If the value is not set or is empty, the directory is guessed from the code-base of
     * the application. For e.g. JARs this would be the directory in which the application-class-jar
     * is located.<br>
     * The value can use settings from defaults by enclosing the name in "%".<br>
     * If {@link #initialize(boolean) initialize(true)} is used, also environment variables can be used.<br>
     * <u>Example</u>:<br>
     * application.basedir = %user.home%/MyApp
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_BASEDIR  = "application.basedir";

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
     * The key of the property used as initial application log level.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called. If the property is not set, the current log level is not modified.
     * @see #initialize(boolean)
     */
    public static String KEY_APPLICATION_LOG_LEVEL = "application.loglevel";

    /**
     * The key of the property used as application name.<br>
     * This key can be changed by application <i>before</i> {@link #initialize(boolean) initialize()}
     * is called.<br>
     * The value can use settings from defaults by enclosing the name in "%".<br>
     * If {@link #initialize(boolean) initialize(true)} is used, also system environment variables can be used.<br>
     * <u>Example</u>:<br>
     * application.properties = %application.basedir%/prefs.ini
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

    private static StorageBase storage_;

    /**
     * Name of property-file to use for persistency, retrieved from "defaultsettings.properties" or System properties on initialization.<br>
     * Used key is {@link #KEY_APPLICATION_PROPERTIES_FILE KEY_APPLICATION_PROPERTIES_FILE}.<br>
     * If the value is not set or empty, persistence is stored via Java-Preferences..
     * @see Application#initialize(java.lang.Class)
     * @see Application#initialize(java.lang.Class, boolean)
     */
    public static String  AppPropertyFile ;

    private static String resolveFromDefaults( String value, Properties defaults )
    {
        // Resolve any %XXX% property references in value.
        Pattern p = Pattern.compile("%([^%]*)%");

        Matcher m = p.matcher(value);

        StringBuilder newPF = new StringBuilder(20);

        int lindex = 0;

        while ( m.find() )
        {
            newPF.append( value.substring(lindex, m.start(0) ) );
            String propValue = defaults.getProperty( m.group(1) );
            if ( propValue != null )
                newPF.append(propValue);
            lindex = m.end(0);
        }
        if ( lindex > 0 )
        {
            if ( lindex < value.length() )
                newPF.append(value.substring(lindex));

            if (Log.isDebugEnabled()) Log.debug( "Value '"+value+"' resolved to '"+newPF+"'" );
            return newPF.toString();
        }
        return value;
    }


    /**
     * Needs to be called by the application as FIRST method before any
     * other functionality is used from this class.<br>
     * The call will initialize several global settings:
     * <ul>
     * <li> {@link com.bw.jtools.Application#AppCompany Application.AppCompany}
     * <li> {@link com.bw.jtools.Application#AppName Application.AppName}
     * <li> {@link com.bw.jtools.Application#AppIconPrefix Application.AppIconPrefix}
     * <li> {@link com.bw.jtools.Application#AppVersion Application.AppVersion}
     * <li> {@link #AppPropertyFile Store.AppPropertyFile}
     * </ul>
     * Don't call this method directly. Please use {@link com.bw.jtools.Application#initialize(java.lang.Class) Application.initialize(Class)}.
     * @param useSysPropsForDefault if true, the defaults are initialized from system-properties before loading the property file.
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
        String appLogLevel = null;
        String appBaseDir = null;
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
            appLogLevel = defaults.getProperty( KEY_APPLICATION_LOG_LEVEL );
            appBaseDir = defaults.getProperty( KEY_APPLICATION_BASEDIR );
        }
        catch (IOException ex)
        {
        }

        // Normalize
        if ( appName    != null && appName .isEmpty() ) appName = null;
        if ( company    != null && company .isEmpty() ) company = null;
        if ( propFile   != null && propFile.isEmpty() ) propFile= null;
        if ( appIconPrefix != null && appIconPrefix.isEmpty() ) appIconPrefix = null;
        if ( appBaseDir != null && appBaseDir.isEmpty() ) appBaseDir = null;

        if ( appLogLevel != null )
        {
            int level = -1;
            if ("NONE".equalsIgnoreCase(appLogLevel))
            {
                level = Log.NONE;
            }
            else if ("ERROR".equalsIgnoreCase(appLogLevel))
            {
                level = Log.ERROR;
            }
            else if ("WARN".equalsIgnoreCase(appLogLevel))
            {
                level = Log.WARN;
            }
            else if ("INFO".equalsIgnoreCase(appLogLevel))
            {
                level = Log.INFO;
            }
            else if ("DEBUG".equalsIgnoreCase(appLogLevel))
            {
                level = Log.DEBUG;
            }
            else
            {
                try
                {
                    level = Integer.parseUnsignedInt(appLogLevel);
                }
                catch ( Exception e)
                {
                    Log.error( KEY_APPLICATION_LOG_LEVEL+": have to be NONE, ERROR, WARN, INFO, DEBUG or a positive integer.");
                }
            }
            if ( level > -1 )
                Log.setLevel( level );
        }



        if ( appName == null )
        {
            Log.warn("Property '"+KEY_APPLICATION_NAME+"' is not set, please check "+DEFAULT_INI);
            if ( Application.AppClass != null )
                appName = Application.AppClass.getSimpleName();
            else
                appName = "MyApp";
        }

        if ( company == null )
        {
            if ( Application.AppClass != null )
            {
                String fullname = Application.AppClass.getPackage().getName();
                if (fullname != null)
                {
                    int firstDot = fullname.indexOf('.');
                    if (firstDot > 0)
                    {
                        int nextDot  = fullname.indexOf('.', firstDot+1);
                        if (nextDot>0)
                        {
                           company = fullname.substring(firstDot+1, nextDot);
                        }
                    }
                }
            }
            if ( company == null || company .isEmpty() )
                company = "MyCompany";
        }

        if ( version == null ) version = "1.0";

        if ( appIconPrefix == null )
        {
            appIconPrefix = appName;
        }

        Application.AppName         = appName;
        Application.AppVersion      = version;
        Application.AppCompany      = company;
        Application.AppIconPrefix   = appIconPrefix;

        Path appBaseFolder = null;
        try
        {
            if ( appBaseDir == null )
            {
                // Get the location of the application jar/class-root
                URL codeLocation = Application.AppClass.getProtectionDomain().getCodeSource().getLocation();
                if (codeLocation != null)
                {
                    appBaseFolder = Paths.get( codeLocation.toURI()).toRealPath();
                }
            }
            else
            {
                appBaseDir = resolveFromDefaults( appBaseDir, defaults );
                appBaseFolder = Paths.get( appBaseDir );
            }
            if ( appBaseFolder != null )
            {
                if ( !Files.exists(appBaseFolder) )
                {
                    Log.warn("Creating none existing application base directory "+appBaseFolder );
                    Files.createDirectories(appBaseFolder);
                }
                else if ( !Files.isDirectory(appBaseFolder) )
                {
                    // Location may be a jar-file. In this case use containing folder.
                    appBaseFolder = appBaseFolder.getParent();
                    if ( !Files.isDirectory(appBaseFolder) )
                        appBaseFolder = null;
                }
                if ( appBaseFolder != null && !Files.isWritable(appBaseFolder) )
                {
                    Log.warn("No write permissions to application base directory "+appBaseFolder );
                }
            }
        }
        catch ( Exception se )
        {
            // Possibly no permissions...
            Log.warn("Failed to access application base directory", se);
            appBaseFolder = null;
        }

        // If folder == null, user-home and application name is used.
        Application.setBaseAppDirectory( appBaseFolder );
        // Put caluclated value back to defaults to feed property-file name resolver below
        Path p = Application.getBaseAppDirectory();
        if (p != null)
            defaults.put( KEY_APPLICATION_BASEDIR, p.toString() );

        if ( propFile != null  )
        {
            propFile = resolveFromDefaults( propFile, defaults );
        }
        AppPropertyFile = propFile;

        PreferencesStorage.PREF_ROOT_KEY =  company+"/"+appName ;
        Path propPath = null;

        if ( AppPropertyFile != null )
        {
            try
            {
                propPath = Paths.get(AppPropertyFile);
                if ( !propPath.isAbsolute() )
                    propPath = Application.getBaseAppDirectory().resolve(propPath);
                storage_ = new FileStorage( propPath, defaults );
            }
            catch (Exception e)
            {
                Log.debug("Failed to use preferences from "+propPath, e);
            }
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
