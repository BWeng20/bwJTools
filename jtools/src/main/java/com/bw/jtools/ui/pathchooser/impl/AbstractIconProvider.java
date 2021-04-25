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

import com.bw.jtools.ui.image.ImageTool;
import com.bw.jtools.ui.pathchooser.PathIconProvider;
import com.bw.jtools.ui.pathchooser.PathInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Abstract base of the Icon-Cache for path icons.<br>
 * Implementations have to provide the method {@link #getIcon(PathInfo)}.<br>
 * Implementations may also support different icon sizes (see {@link #setUseLargeIcons(boolean)}).
 */
public abstract class AbstractIconProvider implements PathIconProvider
{
	/**
	 * The folder icon.
	 */
	private Icon dirIcon = UIManager.getIcon("FileView.directoryIcon");
	private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

	private Font specialIconFont_;
	private HashMap<String, Icon> specialIcons_ = new HashMap<>();

	private int iconGeneration_ = 0;

	{
		updateUIIcons();
	}

	@Override
	public void updateUIIcons()
	{
		dirIcon = UIManager.getIcon("FileView.directoryIcon");
		fileIcon = UIManager.getIcon("FileView.fileIcon");
		updateFont();
		// Don't increased iconGeneration_ here, as general icons are npt affected.
	}

	protected void updateFont()
	{
		Font newFont = UIManager.getFont("Label.font")
								.deriveFont(12f);
		if (!newFont.equals(specialIconFont_))
		{
			specialIconFont_ = newFont;
			specialIcons_.clear();
		}
	}

	@Override
	public Icon getFolderIcon()
	{
		return dirIcon;
	}

	@Override
	public Icon getTextIcon(boolean isFolder, String text)
	{
		final String key = "spec:" + isFolder + "-" + text;
		Icon ic = specialIcons_.get(key);
		if (ic == null)
		{
			Icon base = isFolder ? dirIcon : fileIcon;
			BufferedImage img = ImageTool.createImage(base.getIconWidth(), base.getIconHeight(), true);
			Graphics2D g2 = img.createGraphics();
			base.paintIcon(null, g2, 0, 0);
			g2.dispose();
			ImageTool.drawText(img, specialIconFont_, Color.BLACK, text);
			ic = new ImageIcon(img);
			specialIcons_.put(key, ic);
		}
		return ic;
	}

	protected Icon getDefaultIcon(PathInfo pathInfo)
	{
		return pathInfo.isTraversable() ? dirIcon : fileIcon;
	}

	/**
	 * Returns if large icons shall be used.<br>
	 * Default implementation always return false.<br>
	 * Implementations should override this method if large icons are supported.
	 */
	@Override
	public boolean isUseLargeIcons()
	{
		return false;
	}


	/**
	 * Controls if large icons shall be used.<br>
	 * Default implementation ignored this call.<br>
	 * Implementations should override this method if large icons are supported.
	 */
	@Override
	public void setUseLargeIcons(boolean large)
	{
	}

	@Override
	public int getIconGeneration()
	{
		return iconGeneration_;
	}

	protected void increaseIconGeneration()
	{
		++iconGeneration_;
	}

}
