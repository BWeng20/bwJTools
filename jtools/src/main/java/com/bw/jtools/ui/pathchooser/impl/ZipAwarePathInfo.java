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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

class ZipAwarePathInfo extends PathInfo
{
	private Boolean isTraversable_;
	private Boolean isZip_;
	private Boolean isReadable_;

	/**
	 * Optional cached icon for this path-info.<br>
	 */
	Icon icon_;

	/**
	 * Generation index for {@link #icon_}.
	 */
	int iconGeneration_ = 0;


	public ZipAwarePathInfo(PathInfo parent, Path path)
	{
		super(parent, path);
	}

	@Override
	public boolean isTraversable()
	{
		if (isTraversable_ == null)
			isZip();
		return isTraversable_.booleanValue();
	}

	@Override
	public boolean isReadable()
	{
		if (isReadable_ == null)
			isZip();
		return isReadable_.booleanValue();
	}

	/**
	 * Checks if the file is a zip archive.
	 *
	 * @return True if the file is a zip archive.
	 */
	public boolean isZip()
	{
		if (isZip_ == null)
		{
			try
			{
				final Set<String> supportedViews = path_.getFileSystem()
														.supportedFileAttributeViews();

				BasicFileAttributes basic = null;

				if (supportedViews.contains("dos"))
				{
					DosFileAttributes attr = Files.readAttributes(path_, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					basic = attr;
					isReadable_ = (attr.isHidden() || attr.isSystem()) ? Boolean.FALSE : Boolean.TRUE;
				}
				else if (supportedViews.contains("posix"))
				{
					PosixFileAttributes attr = Files.readAttributes(path_, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					basic = attr;
					isReadable_ = attr.permissions()
									  .contains(PosixFilePermission.OWNER_READ);
				}
				else
				{
					basic = Files.readAttributes(path_, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					isReadable_ = true;
				}
				link_ = basic.isSymbolicLink();

				if (link_)
				{
					basic = Files.readAttributes(path_, BasicFileAttributes.class);
				}

				lastModifiedTime_ = basic.lastModifiedTime();
				size_ = basic.size();

				if (basic.isDirectory())
				{
					isZip_ = Boolean.FALSE;
					isTraversable_ = Boolean.TRUE;
				}
				else if (isZip_ == null)
				{
					// Traversal only possible from system filesystems.
					if (path_.toUri()
							 .getScheme()
							 .equalsIgnoreCase("file"))
					{
						String lfn = fileName_.toLowerCase();
						isZip_ = (lfn.endsWith(".zip") || lfn.endsWith(".jar")) ? Boolean.TRUE : Boolean.FALSE;
					}
					else
						isZip_ = Boolean.FALSE;
					isTraversable_ = isZip_;
				}

			}
			catch (IOException e)
			{
				isReadable_ = Boolean.FALSE;
				isZip_ = Boolean.FALSE;
				isTraversable_ = Boolean.FALSE;
			}
		}
		return isZip_.booleanValue();
	}
}
