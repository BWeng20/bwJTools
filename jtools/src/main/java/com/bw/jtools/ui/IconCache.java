/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bw.jtools.ui;

import com.bw.jtools.Application;
import com.bw.jtools.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Cache for handling icons used in UI.
 */
public class IconCache
{

    private static BufferedImage appImage = null;
    private static BufferedImage appSmallImage = null;
    private static List<BufferedImage> appIconImages = null;

    private static Icon appSmallIcon = null;
    private static Icon dummyIcon = new DummyIcon();

    private static HashMap<String, Icon> icons_ = new HashMap<>();

    /**
     * Load an image from resources of the class
     * {@link com.bw.jtools.Application#initialize(java.lang.Class) Application.initialize()}.<br>
     * The image is not cached. This method simply loads the image from
     * the class-resources.<br>
     * <u>Example</u>:
     * <pre>
     *   // Will load from resources: "/org/myorg/app/icons/MyIcon.png"
     *   IconCache.getImage( org.myorg.app.icons.MyClass.class, "icons/MyIcon.png");
     * </pre>
     *
     * @param name File name of the icon to load.
     * @return The image or null.
     * @throws java.io.IOException If something goes wrong.
     */
    public static BufferedImage getImage(String name) throws IOException
    {
        return getImage(Application.AppClass, name);
    }

    /**
     * Load an image from class resources.<br>
     * The image is not cached. This method simply loads the image from
     * the class-resources.
     *
     * @param clazz The class to use as resource-base.
     * @param name File name of the icon to load.
     * @return The image or null.
     * @throws java.io.IOException If something goes wrong.
     */
    public static BufferedImage getImage(Class clazz, String name) throws IOException
    {
        try( InputStream is =  clazz.getResourceAsStream(name) )
        {
            return ImageIO.read(is);
        }
    }

    /**
     * Gets a icon by name.<br>
     * If the icon is not in cache (key is the name) the icon is loaded
     * from the sub-package "icons" of the specified class.<br>
     *
     * @param clazz The class to use as resource-base.
     * @param name File name of the icon to load.
     * @return The icon or null.
     */
    public static Icon getIcon(Class clazz, String name)
    {
        Icon ic;
        synchronized (icons_)
        {
            ic = icons_.get(name);
            if (ic == null)
            {
                BufferedImage img;
                try
                {
                    img = getImage(clazz, "icons/" + name);
                }
                catch ( Exception e)
                {
                    Log.error("Failed to load icon "+name+".", e);
                    img = null;
                }
                ic = (img != null) ? new ImageIcon(img) : dummyIcon;
                icons_.put(name, ic);
            }
        }
        return ic;
    }

    /**
     * Gets a icon by name.<br>
     * The name has to be a file name of an image inside
     * the sub-package "icons" of the application-class set via
     * {@link com.bw.jtools.Application#initialize(java.lang.Class) Application.initialize()}.<br>
     * <u>Example</u>:
     * <pre>
     *   // Somewhere earlier...
     *   Store.initialize( org.myorg.app.MyClass.class );
     *
     *   ...
     *   // Somewhere else...
     *   // Will load from resources "/org/myorg/app/icons/MyIcon.png"
     *   IconCache.getIcon("MyIcon.png");
     * </pre>
     * @param name File name of the icon to load.
     * @return The icon or null.
     */
    public static Icon getIcon(String name)
    {
        return getIcon(Application.AppClass, name);
    }

    /**
     * Tries to find a application images that fits the preferred size.<br>
     *
     * @param preferredSize The preferred size.
     * @return The image or null if no application image exists.
     */
    public synchronized static BufferedImage getAppImage(int preferredSize)
    {
        if (appImage == null)
        {
            BufferedImage bestImage = null;
            int   bestDiff = Integer.MAX_VALUE;

            List<BufferedImage> images = getAppIconImages();
            for ( BufferedImage i : images )
            {
                final int diff =
                        (Math.abs(preferredSize - i.getHeight()) +
                         Math.abs(preferredSize - i.getWidth ())  )/2;
                if ( diff < bestDiff )
                {
                    bestDiff = diff;
                    bestImage = i;
                }
            }
            return bestImage;
        }
        return appImage;
    }

    /**
     * Get a small application icon.<br>
     * Uses the icon that fits best size 20x20 from the list of application icons.
     * @return The best fitting icon of null if no application image exists.
     */
    public synchronized static Icon getAppSmallIcon()
    {
        if (appSmallIcon == null)
        {
            BufferedImage img = getAppSmallImage();
            appSmallIcon = (img != null) ? new ImageIcon(img) : dummyIcon;
        }
        return appSmallIcon;
    }

    /**
     * Get a small application image.<br>
     * Uses the image that fits best size 20x20 from the list of application images.
     * @return The best fitting image of null if no application image exists.
     */
    public synchronized static BufferedImage getAppSmallImage()
    {
        if (appSmallImage == null)
        {
            appImage = getAppImage( 20 );
        }
        return appSmallImage;
    }

    /**
     * Gets all icons from icons folder with name pattern "[application.iconPrefix]_[size]x[size].png",
     * where "size" is from  16,20,26,28,32,40,64,128.<br>
     * <br>
     * Property "application.iconPrefix" can be defined in "defaultsettings.properties".<br>
     * If "useSysPropsForDefaults" is true in {@link com.bw.jtools.Application#initialize(java.lang.Class, boolean) Application.initialize(Class, useSysPropsForDefaults)}
     * The property can also be defined in system environment or via the command-line -D option.<br>
     * If the property is missing, the application name is used as fallback.
     * <br>
     * <u>A full example:</u>
     * <br>
     * <pre>
     *   // Somewhere at start-up...
     *   Application.initialize( org.myorg.app.MyClass.class );
     * </pre>
     *   The Store will load settings from the file
     *   "org/myorg/app/defaultsettings.properties", e.g.:<br>
     *   <pre>
     *
     *    application.name=My App
     *    application.company=My Corp
     *    application.version=1.0
     *    application.iconPrefix=AppIcon
     *    ...
     * </pre>
     * Then the icon images are e.g.:<br>
     * <ul>
     * <li>org/myorg/app/icons/AppIcon_16x16.png
     * <li>org/myorg/app/icons/AppIcon_32x32.png
     * <li>org/myorg/app/icons/AppIcon_64x64.png
     * <li>...
     * </ul>
     * @return The list of found images.
     */
    public static List<BufferedImage> getAppIconImages()
    {
        if (null == appIconImages)
        {
            appIconImages = new ArrayList<>();

            Arrays.asList("16", "20", "26", "28", "32", "40", "64", "128")
                    .stream().forEach((s) ->
                    {
                        BufferedImage bi;
                        try
                        {
                            bi = getImage("icons/" + Application.AppIconPrefix + "_" + s + "x" + s + ".png");
                        }
                        catch (Exception ex)
                        {
                            /* silently ignorted */
                            bi = null;
                        }
                        if (null != bi)
                        {
                            appIconImages.add(bi);
                        }
                    });
        }
        return appIconImages;
    }

}
