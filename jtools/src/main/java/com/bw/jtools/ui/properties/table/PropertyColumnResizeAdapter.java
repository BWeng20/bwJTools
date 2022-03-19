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
package com.bw.jtools.ui.properties.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Mouse handler that enabled the user to resizes columns via Drag &amp; Drop
 * in the <b>main table area</b>.
 * <p>
 * Per default JTable-columns can only be resized via D&amp;D in the table-header.
 * The Property-table should not have any header, so I needed to re-implement
 * the JTable mouse-handler (check for BasicTableHeaderUI.MouseInputHandler).
 * <p>
 * <p>
 * Some remarks:<br>
 * Column resize only works if a header instance is present - simply to
 * store the currently resized column to prevent the table from re-adjusting
 * after a size change. This means that JTable.setTableHeader(null) can't be
 * used to disable the header. Instead getTableHeader().setUI(null) has
 * to be used to disable it. By this trick the table has still the instance
 * to track resize-operation, but the header is not visible.
 */
public class PropertyColumnResizeAdapter extends MouseInputAdapter
{
	private int mouseXOffset_;
	private JTable table_;
	private JTableHeader header_;

	/**
	 * Cursor to use for resize operations.
	 */
	public static Cursor resizeCursor_ = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

	/**
	 * Normal Cursor.
	 */
	public static Cursor normalCursor_;

	private boolean resizeCursorSet_ = false;
	private TableColumn resizingColumn_;

	/**
	 * Create a Resize-Adapter for the table.
	 * The adapter installs itself into the table.
	 *
	 * @param table The table to resize.
	 */
	public PropertyColumnResizeAdapter(JTable table)
	{
		table_ = table;
		header_ = table.getTableHeader();
		table_.addMouseListener(this);
		table_.addMouseMotionListener(this);
	}


	private TableColumn getResizingColumn(Point p)
	{
		return getResizingColumn(p, table_.columnAtPoint(p));
	}

	private TableColumn getResizingColumn(Point p, int column)
	{
		if (column == -1)
		{
			return null;
		}
		Rectangle r = table_.getCellRect(0, column, true);
		if (Math.abs(p.x - r.x) > 3 && Math.abs(p.x - (r.x + r.width)) > 3)
		{
			return null;
		}
		int midPoint = r.x + r.width / 2;
		int columnIndex;
		if (table_.getComponentOrientation()
				  .isLeftToRight())
		{
			columnIndex = (p.x < midPoint) ? column - 1 : column;
		}
		else
		{
			columnIndex = (p.x < midPoint) ? column : column - 1;
		}
		if (columnIndex == -1)
		{
			return null;
		}
		return table_.getColumnModel()
					 .getColumn(columnIndex);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Point p = e.getPoint();

		int index = table_.columnAtPoint(p);

		if (index != -1)
		{
			// The last 3 pixels + 3 pixels of next column are for resizing
			resizingColumn_ = getResizingColumn(p, index);
			header_.setResizingColumn(resizingColumn_);
			if (resizingColumn_ != null)
			{
				if (table_.getComponentOrientation()
						  .isLeftToRight())
				{
					mouseXOffset_ = p.x - resizingColumn_.getWidth();
				}
				else
				{
					mouseXOffset_ = p.x + resizingColumn_.getWidth();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if ((getResizingColumn(e.getPoint()) != null))
		{
			if (!resizeCursorSet_)
			{
				resizeCursorSet_ = true;
				normalCursor_ = table_.getCursor();
				table_.setCursor(resizeCursor_);
			}
		}
		else if (resizeCursorSet_)
		{
			resizeCursorSet_ = false;
			table_.setCursor(normalCursor_);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		int mouseX = e.getX();

		boolean headerLeftToRight = table_.getComponentOrientation()
										  .isLeftToRight();

		if (resizingColumn_ != null)
		{
			int oldWidth = resizingColumn_.getWidth();
			int newWidth;
			if (headerLeftToRight)
			{
				newWidth = mouseX - mouseXOffset_;
			}
			else
			{
				newWidth = mouseXOffset_ - mouseX;
			}
			resizingColumn_.setWidth(newWidth);

			Container container = table_.getParent()
										.getParent();

			if ((container == null) || !(container instanceof JScrollPane))
			{
				return;
			}

			if (!container.getComponentOrientation()
						  .isLeftToRight()
					&& !table_.getComponentOrientation()
							  .isLeftToRight())
			{
				JViewport viewport = ((JScrollPane) container).getViewport();
				int viewportWidth = viewport.getWidth();
				int diff = newWidth - oldWidth;
				int newTableWidth = table_.getWidth() + diff;

				/* Resize a table */
				Dimension tableSize = table_.getSize();
				tableSize.width += diff;
				table_.setSize(tableSize);

				if ((newTableWidth >= viewportWidth)
						&& (table_.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF))
				{
					Point p = viewport.getViewPosition();
					p.x = Math.max(0, Math.min(newTableWidth - viewportWidth,
							p.x + diff));
					viewport.setViewPosition(p);
					mouseXOffset_ += diff;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		resizingColumn_ = null;
		header_.setResizingColumn(null);
	}


}
