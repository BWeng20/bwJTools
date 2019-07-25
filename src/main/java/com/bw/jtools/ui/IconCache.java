/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bw.jtools.ui;

import static com.bw.jtools.io.IOTool.main_class_;
import com.bw.jtools.persistence.Store;
import com.bw.jtools.Log;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

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
     * Load an image from resources of the class {@link com.bw.jtools.io.IOTool#main_class_ IOTool.main_class_}.<br>
     * The image is not cached. This method simply loads the image from
     * the class-resources.
     *
     * @param name File name of the icon to load.
     * @return The image or null.
     * @throws java.io.IOException If something goes wrong.
     */
    public static BufferedImage getImage(String name) throws IOException
    {
        return getImage(main_class_, name);
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
        return ImageIO.read(clazz.getResourceAsStream(name));
    }

    /**
     * Gets a icon by name.<br>
     * If the icon is not in cache (key is name) the icon is loaded
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
     * Gets a icon by name. The name has to be the file name of an image inside
     * the sub-package "icons"of the application-class set via
     * {@link com.bw.jtools.io.IOTool#main_class_ IOTool.main_class_}.
     * @param name File name of the icon to load.
     * @return The icon or null.
     */
    public static Icon getIcon(String name)
    {
        return getIcon(main_class_, name);
    }

    /**
     * Tries to find a application images that fits the preferred size.
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
     * Gets all icons from icons folder with name pattern "[AppName]_[size]x[size].png",
     * where "size" is from  16,20,26,28,32,40,64,128.<br>
     * Missing icons are skipped silently.
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
                            bi = getImage("icons/" + Store.AppName + "_" + s + "x" + s + ".png");
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
