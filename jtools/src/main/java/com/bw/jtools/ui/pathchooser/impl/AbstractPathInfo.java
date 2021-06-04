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

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Path Info that handles zip/jar archives and caches children.
 */
public abstract class AbstractPathInfo extends PathInfo
{
	protected List<AbstractPathInfo> children_;

	/**
	 * Optional cached icon for this path-info.<br>
	 */
	protected Icon icon_;

	/**
	 * Generation index for {@link #icon_}.
	 */
	protected int iconGeneration_ = 0;

	public AbstractPathInfo(PathInfo parent, Path path)
	{
		super(parent, path);
	}



	/**
	 * Gets children or null if not yet scanned.
	 */
	public List<AbstractPathInfo> getChildren()
	{
		return children_;
	}

	/**
	 * Sets children.
	 * @param children children or null.
	 */
	public void setChildren( List<AbstractPathInfo> children)
	{
		children_ = children;
	}

	public boolean isWatched()
	{
		return isWatched_;
	}

	public void setWatched(boolean watched)
	{
		isWatched_ = watched;
	}

}
