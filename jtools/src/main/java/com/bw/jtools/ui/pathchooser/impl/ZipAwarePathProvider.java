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

import com.bw.jtools.ui.pathchooser.PathIconProvider;
import com.bw.jtools.ui.pathchooser.PathInfo;
import com.bw.jtools.ui.pathchooser.PathInfoProvider;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;

/**
 * Implementation of PathInfoProvider that handles Zip-Archives as folders.
 */
public class ZipAwarePathProvider implements PathInfoProvider
{
	private PathIconProvider icons_;

	public ZipAwarePathProvider(PathIconProvider icons)
	{
		icons_ = icons;
	}

	@Override
	public PathInfo createPathInfo(PathInfo parent, Path path)
	{
		return new ZipAwarePathInfo(parent, path);
	}

	@Override
	public Icon getIcon(PathInfo info)
	{
		if (info instanceof ZipAwarePathInfo)
		{
			ZipAwarePathInfo zi = (ZipAwarePathInfo) info;
			if (zi.isZip())
				return icons_.getTextIcon(true, "ZIP");
			else
			{
				final int igen = icons_.getIconGeneration();
				if (zi.icon_ == null || zi.iconGeneration_ != igen)
				{
					zi.icon_ = icons_.getIcon(info);
					zi.iconGeneration_ = igen;
				}
			}
			return zi.icon_;
		}
		return null;
	}

	@Override
	public Icon getFolderIcon()
	{
		return icons_.getFolderIcon();
	}

	@Override
	public DirectoryStream<Path> getDirectoryStream(PathInfo info) throws IOException
	{
		if (info instanceof ZipAwarePathInfo)
		{
			ZipAwarePathInfo zi = (ZipAwarePathInfo) info;
			if (zi.isZip())
			{
				URI uri = URI.create("jar:" + info.getPath()
												  .toUri());
				FileSystem fileSystem = null;
				synchronized (ZipAwarePathProvider.class)
				{
					try
					{
						fileSystem = FileSystems.getFileSystem(uri);
					}
					catch (FileSystemNotFoundException e)
					{
						fileSystem = FileSystems.newFileSystem(uri, Collections.EMPTY_MAP);
					}
				}

				Iterable<Path> roots = fileSystem.getRootDirectories();
				Iterator<Path> r = roots.iterator();
				if (r.hasNext()) return Files.newDirectoryStream(r.next());
			}
		}
		return Files.newDirectoryStream(info.isLink() ? info.getPath()
															.toRealPath() : info.getPath());
	}

}
