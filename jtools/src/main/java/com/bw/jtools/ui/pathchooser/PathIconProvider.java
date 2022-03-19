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
package com.bw.jtools.ui.pathchooser;

import javax.swing.Icon;

/**
 * Provider for path icons.<br>
 * Implementations should cache icon as calls to this interface is done
 * with high frequency during screen-rendering.
 */
public interface PathIconProvider
{
	/**
	 * Updates any the UI dependent icons.<br>
	 * Have to be called if LAF is changed (e.g. from "updateUI" the controlling component of the instance).
	 */
	public void updateUIIcons();

	/**
	 * Get the default folder icon.
	 */
	public Icon getFolderIcon();

	/**
	 * Gets the file dependent icon for a file.
	 */
	public Icon getIcon(PathInfo path);

	/**
	 * Get number of current icon generation.<br>
	 * The icon generation will be increased relevent arguments for icon generation are changed.
	 * The value can be used to detect of icon needs to be recreated via {@link #getIcon(PathInfo)}
	 */
	public int getIconGeneration();

	/**
	 * Controls if large icons shall be used if the icon provider supports it.<br>
	 * Support for large icons is optional.
	 */
	public void setUseLargeIcons(boolean large);

	/**
	 * Get if large icons are used.
	 */
	public boolean isUseLargeIcons();

}
