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
package com.bw.jtools.ui.pathchooser.impl;

import com.bw.jtools.ui.pathchooser.PathInfo;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Implementation via sun.Shell. May break in future jre versions.<br>
 * Before use call "isSupported" to verify that the current java version is supported and the used
 * classes are accessible.<br>
 */
public class ShellIconProvider extends AbstractIconProvider
{
	protected boolean useLargeIcons_ = false;

	@Override
	public Icon getIcon(PathInfo path)
	{
		if (path == null)
		{
			return null;
		}
		sun.awt.shell.ShellFolder sf;
		Icon ic = null;
		if (path.isFileApiSupported())
		{
			File f;
			try
			{
				f = path.toFile();
			}
			catch (UnsupportedOperationException ue)
			{
				// File API not available
				f = null;
			}
			if (f != null)
			{

				try
				{
					sf = sun.awt.shell.ShellFolder.getShellFolder(f);
					Image img = sf.getIcon(useLargeIcons_);
					if (img != null)
						ic = new ImageIcon(img, sf.getFolderType());

				}
				catch (FileNotFoundException e)
				{
					return null;
				}
				catch (IllegalAccessError ie)
				{
					return null;
				}
			}
		}
		if (ic == null)
		{
			ic = getDefaultIcon(path);
		}
		return ic;
	}

	private static Boolean isSupported_ = null;

	/**
	 * Checks if ShellFolder sub.awt.shell.ShellFolder is available and accessible.
         * @return true if access to shell icons via sun.awt.shell is supported.
	 */
	static public boolean isSupported()
	{
		if (isSupported_ == null)
		{
			try
			{
				sun.awt.shell.ShellFolder.getShellFolder(new File("."));
				isSupported_ = Boolean.TRUE;
			}
			catch (NoClassDefFoundError ne)
			{
				isSupported_ = Boolean.FALSE;
			}
			catch (IllegalAccessError ia)
			{
				isSupported_ = Boolean.FALSE;
			}
			catch (FileNotFoundException e)
			{
				isSupported_ = Boolean.TRUE;
			}
		}
		return isSupported_;
	}

	/**
	 * Returns if large icons shall be used.
	 */
	@Override
	public boolean isUseLargeIcons()
	{
		return useLargeIcons_;
	}

	/**
	 * Controls if large icons shall be used.
	 */
	public void setUseLargeIcons(boolean large)
	{
		if (useLargeIcons_ != large)
		{
			useLargeIcons_ = large;
			increaseIconGeneration();
		}
	}
}
