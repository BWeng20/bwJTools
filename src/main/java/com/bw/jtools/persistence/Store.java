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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import com.bw.jtools.io.IOTool;
import com.bw.jtools.Log;


/**
 * Collection of methods to get and set Persistence Settings.
 */
public class Store
{
    public static final String PREF_ERROR_REPORT_URL = "error.reportToUrl";

    private static IStorage storage_;
    private static Path baseAppDir_;

    private static Properties defaults_;

    public final static String  AppName;
    public final static String  AppVersion;
    public final static String  AppPropertyFile ;

    static
    {

        defaults_ = new Properties();
        String appName = null;
        String company = null;
        String propFile= null;
        String version = null;
        boolean forcePropFile = false;
        try
        {
            InputStream is = IOTool.main_class_.getResourceAsStream("defaultsettings.properties");
            if ( is != null )
            {
                defaults_.load(is);
                is.close();
            }

            appName = defaults_.getProperty("application.name", null);
            company = defaults_.getProperty("application.company", null);
            propFile= defaults_.getProperty("application.properties", null);
            version = defaults_.getProperty("application.version", null);
        }
        catch (IOException ex)
        {
        }

        if ( appName == null || appName .isEmpty() ) appName = "java";
        if ( company == null || company .isEmpty() ) company = "bweng";
        if ( version == null ) version = "1.0";
        if ( propFile== null || propFile.isEmpty() )
        {
            propFile= "prefs.ini";
        }
        else
        {
            forcePropFile = true;
        }


        AppName         = appName;
        AppVersion      = version;
        AppPropertyFile = propFile;

        PreferencesStorage.PREF_ROOT_KEY =  company+"/"+appName ;

        // try to be Portable
        try
        {
            // Get the location of the application jar/class-root
            URL codeLocation = IOTool.main_class_.getProtectionDomain().getCodeSource().getLocation();
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
                    baseAppDir_ = fileFolder.toPath();
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
        if ( baseAppDir_ == null )
            baseAppDir_ = Paths.get( System.getProperty("user.home"), "."+appName );

        Path propPath = null;

        try
        {
            propPath = baseAppDir_.resolve(AppPropertyFile);
            if ( forcePropFile || Files.exists(propPath) )
            {
                storage_ = new FileStorage( propPath );
            }
        }
        catch (Exception e)
        {
            Log.debug("Failed to load preferences from "+propPath, e);
        }

        if ( storage_ == null )
        {
            // Be Persistent
            storage_ = new PreferencesStorage();
            baseAppDir_ = Paths.get( System.getProperty("user.home"), "."+appName );
        }

    }

    private static void transferStorage( IStorage newStorage )
    {
        newStorage.clear();
        List<String> keys = storage_.getKeysWithPrefix("");
        for ( String key : keys )
        {
            newStorage.setString(key, storage_.getString(key));
        }
        storage_ = newStorage;
    }

    public static void enablePreferences(boolean enable)
    {
        if ( isPreferencesEnabled() != enable )
        {
            if ( enable )
            {
                transferStorage( new PreferencesStorage() );
            }
            else
            {
                IStorage old = storage_;
                transferStorage(new FileStorage(baseAppDir_.resolve(AppPropertyFile)));

                old.clear();
            }
        }
    }

    public static boolean isPreferencesEnabled()
    {
        return storage_ instanceof PreferencesStorage;
    }

    public static Path getBaseAppDirectory()
    {
        return baseAppDir_;
    }

    public static void createBaseAppDirectory()
    {
       Path baseAppDataPath = getBaseAppDirectory();
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


    public static String getString(String key, String def)
    {
        String v = storage_.getString(key);
        if ( v == null)
        {
            v = (String)defaults_.getProperty(key);
        }
        if ( v == null)
            return def;
        else
            return v;
    }

    public static double getDouble(String key, double def)
    {
      try
      {
          final String val = Store.getString(key, String.valueOf(def) );
          return Double.parseDouble(val);
      }
      catch ( Exception e)
      {
      }
      return def;
    }

    public static void setDouble(String key, double value)
    {
        Store.setString(key, String.valueOf(value));
    }


    public static int getInt(String key, int def)
    {
      try
      {
          final String val = Store.getString(key, String.valueOf(def) );
          return Integer.parseInt(val);
      }
      catch ( Exception e)
      {
      }
      return def;
    }

    public static void setInt(String key, int value)
    {
        Store.setString(key, String.valueOf(value));
    }


    public static boolean getBoolean(String key, boolean def)
    {
      try
      {
          final String val = Store.getString(key, def ? "On":"Off" );
          return  val.equalsIgnoreCase("On"  ) ||
                  val.equalsIgnoreCase("Yes" )||
                  val.equalsIgnoreCase("True") ;
      }
      catch ( Exception e)
      {
      }
      return def;
    }

    public static void setBoolean(String key, boolean value)
    {
        Store.setString( key, value ? "On" : "Off" );
    }


    public static void setString(String key, String value)
    {
        storage_.setString(key, value);
    }

    public static List<String> getKeysWithPrefix(String prefix)
    {
       return storage_.getKeysWithPrefix(prefix);
    }

    // Sets a preference from an other if not set already (can be used to
    // use other prefs as default)
    public static void copyIfNotSet(String keyFrom, String keyTo)
    {
        if (Store.getString(keyTo, null) == null)
        {
            String pref = Store.getString(keyFrom, null);
            if (pref != null)
                Store.setString(keyTo, pref);
        }
    }

    // Deletes a preference.
    public static void deleteKey(String key)
    {
       storage_.deleteKey(key);
    }

    /**
     * Flush all changes to storage.
     */
    public static void flushStorage()
    {
       storage_.flush();
    }

}
