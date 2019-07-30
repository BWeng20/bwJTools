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
     * The path is deducted from the class-path of the main-class.<br>
     *
     * @return The path to the base-directory.
     * @see #AppClass
     */
    public static Path getBaseAppDirectory()
    {
        return baseAppDir_;
    }

    /**
     * Sets the base application path.<br>
     * If null is given, the path will be "&lt;user.home&gt;.AppName".<br>
     * On initialization set from code-location of Application-class.
     *
     * @param path The path to the base-directory or null.
     * @see #AppClass
     * @see Store#initialize(boolean)
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
     */
    public static Class<?> AppClass = Store.class;

    /**
     * Name of application, retrieved by Store  on initialization.
     * @see Store#KEY_APPLICATION_NAME
     */
    public static String  AppName;

    /**
     * Version of application, retrieved by Store on initialization.
     * @see Store#KEY_APPLICATION_VERSION
     */
    public static String  AppVersion;

    /**
     * Prefix for Application-Icons.
     * @see Store#KEY_APPLICATION_ICON_PREFIX
     */
    public static String  AppIconPrefix;

    /**
     * Company that owns the application.
     * @see Store#KEY_APPLICATION_COMPANY
     */
    public static String  AppCompany;


    protected static Path baseAppDir_;

}
