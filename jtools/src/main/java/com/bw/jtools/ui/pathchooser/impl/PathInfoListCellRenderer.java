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
import com.bw.jtools.ui.pathchooser.PathInfoProvider;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Font;

/**
 * Renderer for file list items.<br>
 * Shows icon and name of {@link PathInfo} items.
 */
public class PathInfoListCellRenderer implements ListCellRenderer<PathInfo>
{
	protected Border emptyBorder_ = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	protected JLabel cell_ = new JLabel();

	protected Border focusSelectedBorder_;
	protected Border focusBorder_;
	protected PathInfoProvider pathInfoProvider_;

	public PathInfoListCellRenderer(PathInfoProvider pathInfoProvider)
	{
		pathInfoProvider_ = pathInfoProvider;
		cell_.setOpaque(true);
		installBorders();
	}

	public void setFont(Font f)
	{
		cell_.setFont(f);
	}

	public void installBorders()
	{
		focusSelectedBorder_ = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
		focusBorder_ = UIManager.getBorder("List.focusCellHighlightBorder");
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends com.bw.jtools.ui.pathchooser.PathInfo> list,
												  com.bw.jtools.ui.pathchooser.PathInfo pathInfo,
												  int index,
												  boolean isSelected,
												  boolean cellHasFocus)
	{
		Border border = null;
		if (cellHasFocus)
		{
			if (isSelected)
				border = focusSelectedBorder_;
			if (border == null)
				border = focusBorder_;
		}
		else
		{
			border = emptyBorder_;
		}
		String fileName = pathInfo.getFileName();

		cell_.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		cell_.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		cell_.setBorder(border);
		cell_.setText(fileName);
		cell_.setIcon(pathInfoProvider_.getIcon(pathInfo));

		return cell_;
	}
}
