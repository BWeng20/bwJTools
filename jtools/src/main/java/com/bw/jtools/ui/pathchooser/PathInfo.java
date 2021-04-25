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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * Holds a path plus additional information.
 */
public abstract class PathInfo
{
	protected final PathInfo parent_;
	protected final Path path_;
	protected final String fileName_;
	protected boolean link_ = false;
	protected boolean isSkipped_ = false;
	protected Boolean fileApiSupported_;
	protected FileTime lastModifiedTime_;
	protected Long size_;

	/**
	 * Get the path.
	 */
	public Path getPath()
	{
		return path_;
	}

	/**
	 * Get the file name.
	 */
	public String getFileName()
	{
		return fileName_;
	}

	/**
	 * True if the path is a symbolic link.
	 */
	public boolean isLink()
	{
		return link_;
	}

	/**
	 * If the item is traversable.<br>
	 * This property reflects the logical view. E.g. supported archives can also be traversed.
	 */
	public abstract boolean isTraversable();

	/**
	 * If the item is readable.<br>
	 */
	public abstract boolean isReadable();

	/**
	 * If the path may support "toFile".
	 */
	public boolean isFileApiSupported()
	{
		if (fileApiSupported_ == null)
		{
			if (parent_ != null)
				fileApiSupported_ = parent_.isFileApiSupported();
			else
				fileApiSupported_ = Boolean.TRUE;
		}
		return fileApiSupported_;
	}

	/**
	 * Sheck if this path should be skipped in views.
	 */
	public boolean isSkipped()
	{
		return isSkipped_;
	}


	/**
	 * Tries to convert the path to a file.
	 *
	 * @return the file or null.
	 */
	public File toFile()
	{
		if (isFileApiSupported())
		{
			try
			{
				return path_.toFile();
			}
			catch (UnsupportedOperationException ue)
			{
				fileApiSupported_ = Boolean.FALSE;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return path_.toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof PathInfo)
		{
			return Objects.equals(((PathInfo) other).path_, path_);
		}
		else
			return false;
	}

	public PathInfo getParent()
	{
		return parent_;
	}

	protected PathInfo(PathInfo parent, Path p)
	{
		isSkipped_ = parent == null && p.toString()
										.equals(p.getFileSystem()
												 .getSeparator());
		parent_ = parent;
		path_ = p;
		Path fn = path_.getFileName();
		fileName_ = (fn == null ? path_ : fn).toString();
	}

}
