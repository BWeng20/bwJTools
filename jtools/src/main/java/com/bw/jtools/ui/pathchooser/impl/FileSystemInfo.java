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

import javax.swing.*;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Hold information about a file-system.
 */
public class FileSystemInfo
{
	public final FileSystem fsys_;
	public final Icon icon_;
	public final String name_;
	public final List<PathInfoFolderNode> roots_ = new ArrayList<>();
	public final FileSystemNode baseNode_;

	public FileSystemInfo(FileSystem fsys, String name, Icon icon, FileSystemNode baseNode)
	{
		this.fsys_ = fsys;
		this.icon_ = icon;
		this.name_ = name;
		this.baseNode_ = baseNode;
		if (baseNode != null)
		{
			baseNode.setFileSystemInfo(this);
		}
	}

}
