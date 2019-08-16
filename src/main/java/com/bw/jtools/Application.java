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
package com.bw.jtools;

import com.bw.jtools.persistence.Store;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds and control information about the application that use this
 * library.<br>
 *
 */
public class Application
{
    /**
     * Need to be called by the application as FIRST method before any
     * other functionality is used from this library.
     * @param mainClass   Application main class. Uses as base for resources.
     * @param useSysPropsForDefaults Use System-Properties as Defaults.
     * @see Store#getString(java.lang.String, java.lang.String)
     */
    static public void initialize( Class mainClass, boolean useSysPropsForDefaults  )
    {
        AppClass = mainClass;
        Store.initialize(useSysPropsForDefaults);

    }

    /**
     * Need to be called by the application as FIRST method before any
     * other functionality is used from this library.<br>
     * System-properties are used for defaults in Store-functions.
     * @param mainClass Application main class. Uses as base for resources.
     * @see #initialize(java.lang.Class, boolean)
     */
    static public void initialize( Class mainClass  )
    {
        AppClass = mainClass;
        Store.initialize(true);
    }

    /**
     * Gets the base application path.<br>
     * For details see {@link #setBaseAppDirectory(java.nio.file.Path) setBaseAppDirectory}
     * @return The path to the base-directory.
     * @see #AppClass
     */
    public static Path getBaseAppDirectory()
    {
        return baseAppDir_;
    }

    /**
     * Sets the base application path.<br>
     * If null is given, the path will be "&lt;user.home&gt;.&lt;AppName&gt;".<br>
     * The default value is set on initialization from code-location of the Application-class.
     *
     * @param path The path to the base-directory or null.
     * @see #AppClass
     * @see #initialize(java.lang.Class)
     * @see #initialize(java.lang.Class, boolean)
     */
    public static void setBaseAppDirectory(Path path)
    {
        if ( path == null )
            baseAppDir_ = Paths.get( System.getProperty("user.home"), "."+AppName );
        else
            baseAppDir_ = path;
    }

    /**
     * Have to be set by application to provide a base for resources.<br>
     * @see #initialize(java.lang.Class)
     * @see #initialize(java.lang.Class, boolean)
     */
    public static Class<?> AppClass = Store.class;

    /**
     * Name of application<br>
     * Value is retrieved from "defaultsettings.properties" or System, for details see {@link Store#initialize(boolean) Store.initialize}.<br>
     * Used property key is {@link Store#KEY_APPLICATION_NAME Store.KEY_APPLICATION_NAME}.<br>
     * If no property value is defines, the simple name of the application class is used, see {@link #AppClass AppClass}<br>
     * If no value is defined and no application class is set, "MyApp" will be used.
     */
    public static String  AppName;

    /**
     * Version of application.<br>
     * Value is retrieved from "defaultsettings.properties" or System, for details see {@link Store#initialize(boolean) Store.initialize}.<br>
     * Used property key is {@link Store#KEY_APPLICATION_VERSION Store.KEY_APPLICATION_VERSION}.<br>
     * If no value is defined, "1.0" will be used.
     */
    public static String  AppVersion;

    /**
     * Prefix for Application-Icons.<br>
     * Value is retrieved from "defaultsettings.properties" or System, for details see {@link Store#initialize(boolean) Store.initialize}.<br>
     * Used property key is {@link Store#KEY_APPLICATION_ICON_PREFIX Store.KEY_APPLICATION_ICON_PREFIX}.<br>
     * If no values is defined, the application name is used, see {@link #AppName AppName}.<br>
     *
     */
    public static String  AppIconPrefix;

    /**
     * Company that owns the application.<br>
     * Value is retrieved from "defaultsettings.properties" or System, for details see {@link Store#initialize(boolean) Store.initialize}.<br>
     * Used property key is {@link Store#KEY_APPLICATION_COMPANY Store.KEY_APPLICATION_COMPANY}.<br>
     * If no values is defined, the value is guessed from application package name, second name.<br>
     * E.g. if application class is "<i>com.mycorp.ui.MyApp</i>" company name will be "<i>mycorp</i>"., see {@link #AppClass AppClass}.
     * @see Store#initialize(boolean)
     *
     */
    public static String  AppCompany;


    protected static Path baseAppDir_;

}
