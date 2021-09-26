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

import javax.swing.*;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * The central interface to provide all the paths information.
 */
public interface PathInfoProvider
{
	/**
	 * Creates a new path info object from path.
	 */
	public PathInfo createPathInfo(Path path);

	/**
	 * Gets a list of all additional file-systems.
	 */
	public Collection<FileSystemInfo> getFileSystemInfos();

	/**
	 * Gets info about an additional file-system.<br>
	 * If the filesystem is not "additional" null is returned.
	 */
	public FileSystemInfo getFileSystemInfo(FileSystem fsys);

	/**
	 * Adds an additional file-system.
	 */
	public void addFileSystem(FileSystemInfo fs);

	public PathInfo createPathInfo(PathInfo parent, Path path);

	public Icon getIcon(PathInfo info);

	public Icon getFolderIcon();

	public List<PathInfo> getChildren(PathInfo info) throws IOException;

	public void addFileSystemListener( FileSystemListener l );
	public void removeFileSystemListener( FileSystemListener l );

}
