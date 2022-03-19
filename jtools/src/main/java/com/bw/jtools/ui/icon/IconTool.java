/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.ui.icon;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.image.ImageTool;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Cache for handling icons used in UI.
 */
public class IconTool extends ImageTool
{
	private static Icon appSmallIcon = null;
	private static Icon dummyIcon = new DummyIcon();

	private static HashMap<String, Icon> icons_ = new HashMap<>();


	/**
	 * Gets a icon by name.<br>
	 * If the icon is not in cache (key is the name) the icon is loaded
	 * from the sub-package "icons" of the specified class.<br>
	 *
	 * @param clazz The class to use as resource-base.
	 * @param name  File name of the icon to load.
	 * @return The icon or null.
	 */
	public static Icon getIcon(Class<?> clazz, String name)
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
				catch (Exception e)
				{
					Log.error("Failed to load icon " + name + ".", e);
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
	 *
	 * @param name File name of the icon to load.
	 * @return The icon or null.
	 */
	public static Icon getIcon(String name)
	{
		return getIcon(Application.AppClass, name);
	}

	/**
	 * Get a small application icon.<br>
	 * Uses the icon that fits best size 20x20 from the list of application icons.
	 *
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

}
