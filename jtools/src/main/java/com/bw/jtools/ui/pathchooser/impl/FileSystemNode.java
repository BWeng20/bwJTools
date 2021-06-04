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

import com.bw.jtools.ui.pathchooser.FileSystemInfo;

/**
 * Tree base-node for a file-system.
 */
public class FileSystemNode extends SystemTreeNode
{
	public FileSystemNode()
	{
		super(null);
	}

	public void setFileSystemInfo(FileSystemInfo fsys)
	{
		setUserObject(fsys);
	}

	public FileSystemInfo getFileSystemInfo()
	{
		return (FileSystemInfo) getUserObject();
	}

	@Override
	public String toString()
	{
		FileSystemInfo fi = getFileSystemInfo();
		return fi == null ? "" : fi.name_;
	}


}
