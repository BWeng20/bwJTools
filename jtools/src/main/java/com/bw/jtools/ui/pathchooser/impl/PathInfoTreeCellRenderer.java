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
import com.bw.jtools.ui.pathchooser.PathInfo;
import com.bw.jtools.ui.pathchooser.PathInfoProvider;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Renderer for folder tree items.<br>
 * Shows icon and name of {@link PathInfoFolderNode} user objects.
 */
public class PathInfoTreeCellRenderer extends DefaultTreeCellRenderer
{
	private PathInfoProvider handler_;
	private Object currentNode;

	public PathInfoTreeCellRenderer(PathInfoProvider handler)
	{
		handler_ = handler;
	}

	@Override
	public Icon getOpenIcon()
	{
		if (currentNode instanceof PathInfoFolderNode)
		{
			final PathInfoFolderNode fn = (PathInfoFolderNode) currentNode;
			final PathInfo pn = (fn == null) ? null : fn.getPathInfo();

			if (pn != null)
			{
				return handler_.getIcon(pn);
			}
		}
		else if (currentNode instanceof FileSystemNode)
		{
			final FileSystemInfo fi = ((FileSystemNode) currentNode).getFileSystemInfo();
			if (fi != null)
			{
				return fi.icon_;
			}
		}
		return null;
	}

	@Override
	public Icon getClosedIcon()
	{
		return getOpenIcon();
	}

	@Override
	public Icon getLeafIcon()
	{
		return getOpenIcon();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		currentNode = value;
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

	}

}
