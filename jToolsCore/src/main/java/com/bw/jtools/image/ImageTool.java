/*
 * (c) copyright 2022 Bernd Wengenroth
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
package com.bw.jtools.image;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.UITool;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Tools for Image manipulation
 */
public class ImageTool
{
	private static BufferedImage appImage = null;
	private static BufferedImage appSmallImage = null;
	private static List<BufferedImage> appIconImages = null;

	private static HashMap<String, BufferedImage> images_ = new HashMap<>();

	/**
	 * Load an image from resources of the class
	 * {@link Application#initialize(Class) Application.initialize()}.<br>
	 * This method loads the image from the class-resources and caches it in memory.<br>
	 * <u>Example</u>:
	 * <pre>
	 *   // Will load from resources: "/org/myorg/app/icons/MyIcon.png"
	 *   IconCache.getImage( org.myorg.app.icons.MyClass.class, "icons/MyIcon.png");
	 * </pre>
	 *
	 * @param name File name of the icon to load.
	 * @return The image or null.
	 * @throws IOException If something goes wrong.
	 */
	public static BufferedImage getImage(String name) throws IOException
	{
		return getImage(Application.AppClass, name);
	}

	/**
	 * Load an image from class resources and tries to create a compatible image from it.<br>
	 * This method loads the image from the class-resources and caches it in memory.
	 *
	 * @param clazz The class to use as resource-base.
	 * @param name  File name of the image to load.
	 * @return The image or null.
	 * @throws IOException If something goes wrong.
	 */
	public static BufferedImage getImage(Class<?> clazz, String name) throws IOException
	{
		return getImage(clazz, name, -1, -1);
	}

	/**
	 * Same as {@link #getImage(Class, String)} but exceptions are caught.
	 *
	 * @param name File name of the icon to load.
	 * @return The image or null.
	 */
	public static BufferedImage getImageSafe(Class<?> clazz, String name)
	{
		try
		{
			return getImage(clazz, name);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Load a (optionally) scaled and tined image from class resources and tries to create a compatible image from it.<br>
	 * This method loads the image from the class-resources and caches it in memory.<br>
	 *
	 * @param clazz  The class to use as resource-base.
	 * @param name   File name of the icon to load.
	 * @param width  The target width or -1 to use original width.
	 * @param height The target height or -1 to use original height.
	 * @return The image or null.
	 * @throws IOException If something goes wrong.
	 */
	public static BufferedImage getImage(Class<?> clazz, String name, int width, int height) throws IOException
	{
		BufferedImage image;

		final String key = clazz.getName() + ":" + name + ":" + width + ":" + height;

		synchronized (images_)
		{
			image = images_.get(key);
			if (image == null && !images_.containsKey(key))
			{
				try (InputStream is = clazz.getResourceAsStream(name))
				{
					if (is == null)
					{
						images_.put(key, null);
						throw new IOException("Resource '" + name + "' not found for class " + clazz.getName());
					}
					image = ImageIO.read(is);
					if (image != null)
					{
						try
						{
							GraphicsConfiguration gConf = GraphicsEnvironment
									.getLocalGraphicsEnvironment()
									.getDefaultScreenDevice()
									.getDefaultConfiguration();

							final ColorModel orgCM = image.getColorModel();
							final ColorModel optCM = gConf.getColorModel();

							if (!orgCM.equals(optCM))
							{
								if (Log.isDebugEnabled())
								{
									Log.debug("Create compatible image '" + name + "': " + orgCM + " -> " + optCM);
								}
								BufferedImage compatibleImage;
								Graphics2D g2;
								if (width == -1 && height == -1)
								{
									compatibleImage = gConf.createCompatibleImage(image.getWidth(), image.getHeight(),
											image.getTransparency());
									g2 = compatibleImage.createGraphics();
									g2.drawImage(image, 0, 0, null);
								}
								else
								{
									if (width == -1) width = image.getWidth();
									if (height == -1) height = image.getHeight();

									compatibleImage = gConf.createCompatibleImage(width, height, image.getTransparency());
									g2 = compatibleImage.createGraphics();
									g2.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
											, 0, 0, null);
								}
								g2.dispose();
								image = compatibleImage;
							}
						}
						catch (Exception e)
						{
							// Possible e.g. in headless environment.
						}
					}
					images_.put(key, image);
				}
			}
		}
		return image;
	}

	/**
	 * Colorize an image.
	 *
	 * @param original The original
	 * @param color    The color.
	 * @return the colorized image.
	 */
	public static BufferedImage colorizeImage(BufferedImage original, Paint color)
	{
		if (original != null)
		{
			final int width = original.getWidth();
			final int height = original.getHeight();

			BufferedImage colorizedCopy = createImage(width, height, original.getTransparency());
			Graphics2D g2 = colorizedCopy.createGraphics();
			g2.drawImage(original, 0, 0, null);
			g2.setComposite(AlphaComposite.SrcAtop);
			g2.setPaint(color);
			g2.fillRect(0, 0, width, height);
			g2.dispose();
			return colorizedCopy;
		}
		else
			return null;
	}

	/**
	 * Get device compatible image.<br>
	 *
	 * @param width            The desired width.
	 * @param height           The desired height.
	 * @param transparencyMode Transparency mode.
	 * @return The image.
	 * @see Transparency#OPAQUE
	 * @see Transparency#BITMASK
	 * @see Transparency#TRANSLUCENT
	 */
	public static BufferedImage createImage(int width, int height, int transparencyMode)
	{
		GraphicsConfiguration gConf = GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();

		return gConf.createCompatibleImage(width, height, transparencyMode);
	}

	/**
	 * Get device compatible image.<br>
	 *
	 * @param width        The desired width.
	 * @param height       The desired height.
	 * @param transparency True if image shall support transparency.
	 * @return The image.
	 */
	public static BufferedImage createImage(int width, int height, boolean transparency)
	{
		return createImage(width, height, transparency ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
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
			int bestDiff = Integer.MAX_VALUE;

			List<BufferedImage> images = getAppIconImages();
			for (BufferedImage i : images)
			{
				final int diff =
						(Math.abs(preferredSize - i.getHeight()) +
								Math.abs(preferredSize - i.getWidth())) / 2;
				if (diff < bestDiff)
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
	 * Get a small application image.<br>
	 * Uses the image that fits best size 20x20 from the list of application images.
	 *
	 * @return The best fitting image of null if no application image exists.
	 */
	public synchronized static BufferedImage getAppSmallImage()
	{
		if (appSmallImage == null)
		{
			appImage = getAppImage(20);
		}
		return appSmallImage;
	}

	/**
	 * Gets all icons from icons folder with name pattern "[application.iconPrefix]_[size]x[size].png",
	 * where "size" is from  16,20,26,28,32,40,64,128.<br>
	 * <br>
	 * Property "application.iconPrefix" can be defined in "defaultsettings.properties".<br>
	 * If "useSysPropsForDefaults" is true in {@link Application#initialize(Class, boolean) Application.initialize(Class, useSysPropsForDefaults)}
	 * The property can also be defined in system environment or via the command-line -D option.<br>
	 * If the property is missing, the application name is used as fallback.
	 * <br>
	 * <u>A full example:</u>
	 * <br>
	 * <pre>
	 *   // Somewhere at start-up...
	 *   Application.initialize( org.myorg.app.MyClass.class );
	 * </pre>
	 * The Store will load settings from the file
	 * "org/myorg/app/defaultsettings.properties", e.g.:<br>
	 * <pre>
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
	 *
	 * @return The list of found images.
	 */
	public static List<BufferedImage> getAppIconImages()
	{
		if (null == appIconImages)
		{
			appIconImages = new ArrayList<>();

			Arrays.asList("16", "20", "26", "28", "32", "40", "64", "128")
				  .stream()
				  .forEach((s) ->
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

	/**
	 * Creates an image with a centered text.
	 */
	public static BufferedImage createTextImage(int width, int height, Font font, Paint paint, String text)
	{
		final BufferedImage img = createImage(width, height, true);
		drawText(img, font, paint, text);

		return img;
	}

	/**
	 * Draws centered text on top of an image.
	 */
	public static void drawText(BufferedImage image, Font font, Paint paint, String text)
	{
		final int width = image.getWidth();
		final int height = image.getHeight();
		final Graphics2D g2 = image.createGraphics();

		g2.setPaint(paint);
		g2.setFont(font);
		UITool.addDesktopFontHints( g2 );
		final Rectangle2D bounds = font.getStringBounds(text, g2.getFontRenderContext());
		g2.drawString(text, (int) (((width - bounds.getWidth()) / 2 )+0.5),
				(int) (((height - bounds.getHeight()) / 2) + g2.getFontMetrics().getAscent()+0.5));
		g2.dispose();
	}

	/**
	 * Create a checkerboard paint where each field is width x height.
	 */
	public static BufferedImage createCheckerboardImage(Color c1, Color c2, int width, int height )
	{
		return createCheckerboardImage(c1, c2, width, height, 2, 2);
	}

	/**
	 * Create a checkerboard paint where each field is width x height.
	 * Number of fields in each direction can be specified with column and rows.
	 */
	public static BufferedImage createCheckerboardImage(Color c1, Color c2, int width, int height, int columns, int rows ) {
		final BufferedImage img = createImage(columns*width, rows*height, false);
		Graphics2D g2 = img.createGraphics();
		Color tmp;
		for ( int r = 0 ; r<rows; ++r)
		{
			for (int c = 0; c < columns; ++c)
			{
				g2.setColor(c1);
				g2.fillRect(c * width, r * height, width, height);
				// Swap colors
				tmp = c2;
				c2 = c1;
				c1 = tmp;
			}
			tmp = c2;
			c2 = c1;
			c1 = tmp;
		}
		g2.dispose();
		return img;
	}


}
