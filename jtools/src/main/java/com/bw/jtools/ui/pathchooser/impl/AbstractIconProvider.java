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

import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.pathchooser.JPathChooser;
import com.bw.jtools.ui.pathchooser.PathIconProvider;
import com.bw.jtools.ui.pathchooser.PathInfo;

import javax.swing.Icon;
import javax.swing.UIManager;

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
	private Icon dirIcon16_;
	private Icon dirIcon24_;
	private Icon fileIcon16_;
	private Icon fileIcon24_;

	private int iconGeneration_ = 0;

	{
		updateUIIcons();
	}

	@Override
	public void updateUIIcons()
	{
		dirIcon16_ = IconTool.getIcon(JPathChooser.class, "folder16.png");
		if ( dirIcon16_ == null )
			dirIcon16_ = UIManager.getIcon("FileView.directoryIcon");
		dirIcon24_ = IconTool.getIcon(JPathChooser.class, "folder24.png");
		if ( dirIcon24_ == null )
			dirIcon24_ = dirIcon16_;

		fileIcon16_ = IconTool.getIcon(JPathChooser.class, "file16.png");
		if ( fileIcon16_ == null )
			fileIcon16_ = UIManager.getIcon("FileView.fileIcon");
		fileIcon24_ = IconTool.getIcon(JPathChooser.class, "file24.png");
		if ( fileIcon24_ == null )
			fileIcon24_ = fileIcon16_;

		// Don't increased iconGeneration_ here, as general icons are not affected.
	}

	@Override
	public Icon getFolderIcon()
	{
		return isUseLargeIcons() ? dirIcon24_ : dirIcon16_;
	}

	public Icon getFileIcon()
	{
		return isUseLargeIcons() ? fileIcon24_ : fileIcon16_;
	}

	protected Icon getDefaultIcon(PathInfo pathInfo)
	{
		return pathInfo.isTraversable() ? getFolderIcon() : getFileIcon() ;
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
