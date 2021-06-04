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

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of PathInfoProvider that handles Zip-Archives as folders.
 */
public class ZipAwarePathProvider extends AbstractPathProvider
{
	public ZipAwarePathProvider(PathIconProvider icons)
	{
		super(icons);
	}

	private static Icon zipIcon16_ = IconTool.getIcon(JPathChooser.class, "zip16.png");
	private static Icon zipIcon24_ = IconTool.getIcon(JPathChooser.class, "zip24.png");

	@Override
	public PathInfo createPathInfo(PathInfo parent, Path path)
	{
		return new ZipAwarePathInfo(parent, path);
	}

	@Override
	protected Icon createIcon(AbstractPathInfo info)
	{
		if (info instanceof ZipAwarePathInfo)
		{
			ZipAwarePathInfo zi = (ZipAwarePathInfo) info;
			if (zi.isZip())
				return icons_.isUseLargeIcons() ? zipIcon24_ : zipIcon16_;
		}
		return super.createIcon(info);
	}

	@Override
	protected List<AbstractPathInfo> scanChildren( AbstractPathInfo info ) throws IOException
	{
		ZipAwarePathInfo zi = (ZipAwarePathInfo)info;

		List<AbstractPathInfo> result;
		if (zi.isZip())
		{
			URI uri = URI.create("jar:" + zi.getPath()
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
			if (r.hasNext())
				result = scanPath(zi,r.next());
			else
				result = Collections.EMPTY_LIST;
		}
		else if ( zi.isTraversable() )
			result = scanPath(zi, zi.getPath());
		else
			result = Collections.EMPTY_LIST;
		return result;
	}

}
