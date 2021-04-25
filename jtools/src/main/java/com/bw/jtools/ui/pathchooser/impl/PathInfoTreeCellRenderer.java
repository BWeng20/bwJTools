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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Renderer for folder tree items.<br>
 * Shows icon and name of {@link PathInfoFolderNode} user objects.
 * For other objects the string representation is shown without additional icon.<br>
 * Selection- and focus-state are shown via borders.
 *
 * @see #selectionBackground_
 * @see #selectionForeground_
 */
public class PathInfoTreeCellRenderer extends JLabel implements TreeCellRenderer
{
	/**
	 * Border used for focused and selected nodes.
	 */
	private Border focusSelectedBorder_;
	/**
	 * Border used for focused but not selected nodes.
	 */
	private Border focusBorder_;
	/**
	 * Border used for neither focused nor selected nodes.
	 */
	private Border emptyBorder_ = BorderFactory.createEmptyBorder(1, 1, 1, 11);

	private Border gabBorder_ = BorderFactory.createEmptyBorder(0, 0, 0, 10);

	private PathInfoProvider handler_;

	/**
	 * Color used for selected nodes.
	 */
	private Color selectionForeground_ = Color.BLUE;
	/**
	 * Background-color used for selected nodes.
	 */
	private Color selectionBackground_ = Color.WHITE;

	/**
	 * Color used for normal nodes.
	 */
	private Color foreground_ = Color.BLACK;
	/**
	 * Background-color used for normal nodes.
	 */
	private Color background_ = Color.WHITE;


	public PathInfoTreeCellRenderer(PathInfoProvider handler)
	{
		handler_ = handler;
		setOpaque(true);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		setForeground(selected ? selectionForeground_ : foreground_);
		setBackground(selected ? selectionBackground_ : background_);

		if (value instanceof PathInfoFolderNode)
		{
			final PathInfoFolderNode fn = (PathInfoFolderNode) value;
			final PathInfo pn = (fn == null) ? null : fn.getPathInfo();

			if (pn != null)
			{
				setText(pn.getFileName());
				setIcon(handler_.getIcon(pn));
			}
			else
			{
				setText("");
				setIcon(null);
			}
		}
		else if (value instanceof FileSystemNode)
		{
			final FileSystemInfo fi = ((FileSystemNode) value).getFileSystemInfo();

			if (fi != null)
			{
				setText(fi.name_);
				setIcon(fi.icon_);
			}
			else
			{
				setText("");
				setIcon(null);
			}
		}
		else if (value instanceof DefaultMutableTreeNode)
		{
			setForeground(Color.GRAY);
			setText(String.valueOf(((DefaultMutableTreeNode) value).getUserObject()));
			setIcon(null);
		}
		else
		{
			setText("");
			setIcon(null);
		}

		Border border = null;
		if (hasFocus)
		{
			if (selected)
				border = focusSelectedBorder_;
			if (border == null)
				border = focusBorder_;
		}
		else
		{
			border = emptyBorder_;
		}
		setBorder(new CompoundBorder(border, gabBorder_));
		return this;
	}

	@Override
	public void updateUI()
	{
		super.updateUI();

		if (handler_ != null)
		{

			Border gab = BorderFactory.createEmptyBorder(0, 0, 0, 10);
			focusSelectedBorder_ = new CompoundBorder(UIManager.getBorder("List.focusSelectedCellHighlightBorder"), gab);
			focusBorder_ = new CompoundBorder(UIManager.getBorder("List.focusCellHighlightBorder"), gab);
			selectionForeground_ = UIManager.getColor("List.selectionForeground");
			selectionBackground_ = UIManager.getColor("List.selectionBackground");
			foreground_ = UIManager.getColor("List.foreground");
			background_ = UIManager.getColor("List.background");

			setIcon(handler_.getFolderIcon());
			setText("");
			setBorder(new CompoundBorder(emptyBorder_, gabBorder_));
		}
	}
}
