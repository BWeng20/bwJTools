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
package com.bw.jtools.io;

import com.bw.jtools.Log;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * To get a file from a path is normally very simple and vice versa, so there is no need to use such adapter.<br>
 * But for some Path implementations "toFile" is not supported - e.g. for ZipPaths, used by ZipFileSystem.<br>
 */
public class PathFileAdapter extends File
{
	protected Path path_;
	protected String fileName_;
	protected String virtualPath_;
	protected String storeName_;
	protected boolean virtual_;

	/**
	 * Creates a new Adapter.
	 *
	 * @param p       The path.
	 * @param virtual Use true for virtual file-systems to add the store-nbame to the path.
	 */
	public PathFileAdapter(Path p, boolean virtual)
	{
		super("");
		path_ = p;
		Path fn = p.getFileName();
		fileName_ = fn == null ? "" : fn.toString();
		virtual_ = virtual;

		try
		{
			FileSystem fsys = p.getFileSystem();
			FileStore fs = fsys.provider()
							   .getFileStore(p);
			final String sep = fsys.getSeparator();

			storeName_ = fs.name();
			if (storeName_.endsWith(sep))
				storeName_ = storeName_.substring(0, storeName_.length() - sep.length());

			String path = p.toString();
			if (!sep.equals(File.separator))
				path = path.replace(sep, File.separator);
			if (virtual)
				virtualPath_ = storeName_ + path;
			else
				virtualPath_ = path;
		}
		catch (Exception e)
		{
		}
	}

	protected boolean changePermissions(boolean add, PosixFilePermission... permission)
	{
		try
		{
			boolean done = false;

			PosixFileAttributeView posixView = Files.getFileAttributeView(path_, PosixFileAttributeView.class);
			if (posixView != null)
			{
				HashSet<PosixFilePermission> permissions = new HashSet<>(posixView.readAttributes()
																				  .permissions());
				boolean changed = false;
				for (PosixFilePermission p : permission)
					if (add)
						changed = permissions.add(p) || changed;
					else
						changed = permissions.remove(p) || changed;
				if (changed)
				{
					Files.setPosixFilePermissions(path_, permissions);
				}
				return true;
			}

			DosFileAttributeView dosView = Files.getFileAttributeView(path_, DosFileAttributeView.class);
			if (dosView != null)
			{
				for (PosixFilePermission p : permission)
				{
					if (p == PosixFilePermission.OWNER_WRITE)
					{
						done = true;
						dosView.setReadOnly(!add);
					}
				}
				return done;
			}
		}
		catch (Exception e)
		{
			Log.error("Failed to change path attributes", e);
		}
		return false;
	}


	@Override
	public boolean exists()
	{
		return Files.exists(path_);
	}

	@Override
	public int hashCode()
	{
		return virtualPath_.hashCode();
	}

	@Override
	public String getName()
	{
		return fileName_;
	}

	@Override
	public String getPath()
	{
		return virtualPath_;
	}

	@Override
	public boolean isDirectory()
	{
		final boolean result = Files.isDirectory(path_);
		if (Log.isDebugEnabled())
			Log.debug(virtualPath_ + ":isDirectory -> " + result);
		return result;
	}

	@Override
	public boolean isAbsolute()
	{
		return path_.isAbsolute();
	}

	@Override
	public String getAbsolutePath()
	{
		return virtualPath_;
	}

	@Override
	public File getAbsoluteFile()
	{
		return new PathFileAdapter(path_.toAbsolutePath(), virtual_);
	}

	@Override
	public File getCanonicalFile()
	{
		return this;
	}

	@Override
	public File getParentFile()
	{
		File result;

		final Path parent = path_.getParent();
		if (parent == null || parent.toString()
									.equals("/"))
			result = new File(storeName_);
		else
			result = parent == null ? null : new PathFileAdapter(parent, false);

		if (Log.isDebugEnabled())
			Log.debug(virtualPath_ + ":getParentFile -> " + result);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PathFileAdapter)
			return ((PathFileAdapter) obj).path_.equals(path_);
		else
			return false;
	}

	@Override
	public String[] list()
	{
		if (Log.isDebugEnabled())
			Log.debug(virtualPath_ + ":list()");
		return Arrays.stream(listFiles())
					 .map(File::getName)
					 .toArray(String[]::new);
	}

	@Override
	public File[] listFiles()
	{
		if (Log.isDebugEnabled())
			Log.debug(virtualPath_ + ":listFiles()");

		ArrayList<File> files = new ArrayList<>();
		try
		{
			try (DirectoryStream<Path> dirContentStream = Files.newDirectoryStream(path_))
			{
				for (Path path : dirContentStream)
					files.add(new PathFileAdapter(path, virtual_));
			}

		}
		catch (Exception e)
		{
		}
		return files.toArray(new File[0]);
	}

	@Override
	public boolean canRead()
	{
		return Files.isReadable(path_);
	}

	@Override
	public boolean setReadable(boolean readable, boolean ownerOnly)
	{
		if (ownerOnly)
			return changePermissions(readable, PosixFilePermission.OWNER_READ);
		else
			return changePermissions(readable,
					PosixFilePermission.OWNER_READ,
					PosixFilePermission.GROUP_READ,
					PosixFilePermission.OTHERS_READ);
	}


	@Override
	public boolean canWrite()
	{
		return Files.isWritable(path_);
	}

	@Override
	public boolean setWritable(boolean writable, boolean ownerOnly)
	{
		if (ownerOnly)
			return changePermissions(writable, PosixFilePermission.OWNER_WRITE);
		else
			return changePermissions(writable,
					PosixFilePermission.OWNER_WRITE,
					PosixFilePermission.GROUP_WRITE,
					PosixFilePermission.OTHERS_WRITE);
	}

	@Override
	public boolean canExecute()
	{
		return Files.isExecutable(path_);

	}

	@Override
	public boolean setExecutable(boolean executable, boolean ownerOnly)
	{
		if (ownerOnly)
			return changePermissions(executable, PosixFilePermission.OWNER_EXECUTE);
		else
			return changePermissions(executable,
					PosixFilePermission.OWNER_EXECUTE,
					PosixFilePermission.GROUP_EXECUTE,
					PosixFilePermission.OTHERS_EXECUTE);
	}
}
