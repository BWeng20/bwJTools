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

import com.bw.jtools.Log;
import com.bw.jtools.ui.pathchooser.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Abstract base for PathInfoProvider with some base functionality.
 */
public abstract class AbstractPathProvider implements PathInfoProvider
{
	protected PathIconProvider icons_;
	private List<FileSystemInfo> fileSystems_ = new ArrayList<>();
	private List<FileSystemListener> fileSystemListeners_ = new ArrayList<>();

	protected AbstractPathProvider(PathIconProvider icons)
	{
		icons_ = icons;
	}

	@Override
	public PathInfo createPathInfo(Path path)
	{
		if (path == null)
			return null;
		else
			return createPathInfo(createPathInfo(path.getParent()), path);
	}


	@Override
	public Icon getIcon(PathInfo info)
	{
		if (info instanceof AbstractPathInfo)
		{
			AbstractPathInfo pi = (AbstractPathInfo)info;
			final int igen = icons_.getIconGeneration();
			if (pi.icon_ == null || pi.iconGeneration_ != igen)
			{
				pi.icon_ = createIcon(pi);
				pi.iconGeneration_ = igen;
			}
			return pi.icon_;
		}
		return null;
	}

	/**
	 * Creates a new icon. Default implementation just calls the icon-provider.
	 */
	protected Icon createIcon( AbstractPathInfo pi )
	{
		return icons_.getIcon(pi);
	}

	@Override
	public Icon getFolderIcon()
	{
		return icons_.getFolderIcon();
	}

	@Override
	public List<PathInfo> getChildren(PathInfo info) throws IOException
	{
		if (info instanceof AbstractPathInfo)
		{
			AbstractPathInfo zi = (AbstractPathInfo) info;
			if ( zi.getChildren() == null )
				zi.setChildren( scanChildren(zi) );
			return (List)zi.getChildren();
		}
		Path dir = info.isLink() ? info.getPath()
									   .toRealPath() : info.getPath();
		return (List)scanPath(info, dir);
	}

	@Override
	public FileSystemInfo getFileSystemInfo(FileSystem fsys)
	{
		for (FileSystemInfo fsysi : fileSystems_)
		{
			if (fsysi.fsys_ == fsys)
			{
				return fsysi;
			}
		}
		return null;
	}

	@Override
	public void addFileSystem(FileSystemInfo fsysInfo)
	{
		Log.debug("New file system: " + fsysInfo.fsys_);
		fileSystems_.add(fsysInfo);
		fileFileSystemAdded( fsysInfo );
	}

	@Override
	public Collection<FileSystemInfo> getFileSystemInfos()
	{
		return Collections.unmodifiableCollection(fileSystems_);
	}


	/**
	 * Get list of children of a path.
	 * @param info Used as parent to create path info objects.
	 * @param path Folder to scan.
	 */
	protected List<AbstractPathInfo> scanPath( PathInfo info, Path path ) throws IOException
	{
		List<AbstractPathInfo> result = new ArrayList<>();
		try (DirectoryStream<Path> ds =  Files.newDirectoryStream(path))
		{
			for (Path p : ds)
			{
				AbstractPathInfo pf =  (AbstractPathInfo)createPathInfo(info, p);
				if (pf.isReadable())
				{
					result.add(pf);
				}
			}
		}
		return result;
	}

	protected void fileFileSystemAdded( FileSystemInfo fsys )
	{
		List<FileSystemListener> lcpy = new ArrayList<>(fileSystemListeners_);
		for ( FileSystemListener l : lcpy )
			l.fileSystemAdded(fsys);
	}

	@Override
	public void addFileSystemListener( FileSystemListener l )
	{
		removeFileSystemListener(l);
		fileSystemListeners_.add(l);
	}

	@Override
	public void removeFileSystemListener( FileSystemListener l )
	{
		Iterator<FileSystemListener> ls = fileSystemListeners_.iterator();
		while ( ls.hasNext() )
		{
			if ( l == ls.next() )
				ls.remove();
		}
	}


	/**
 	 * Scans path-info for a list of children of a path-info.
	 * Used to fill children-cache off the AbstractPathInfo.
	 */
	protected abstract List<AbstractPathInfo> scanChildren( AbstractPathInfo info ) throws IOException;

}
