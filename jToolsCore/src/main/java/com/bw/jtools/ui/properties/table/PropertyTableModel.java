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

import org.netbeans.swing.outline.OutlineModel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for the Property-Table.<br>
 * The CellEditor stores changes directly in the nodes, without interaction with the model.
 * So changes can be notified as soon as possible,
 * but default table model would do this only after the cell loose focus.<br>
 * This approach of the default implementation works only if the
 * application doesn't need to react on changes directly on edit. <br>
 */
public class PropertyTableModel implements TableModel
{
	private List<TableModelListener> listeners_ = new ArrayList<TableModelListener>();
	private OutlineModel outlineModel_;
	private boolean editable_ = true;

	/**
	 * Creates an new empty table model.
	 */
	public PropertyTableModel()
	{
	}

	/**
	 * Sets the model editable or read-only.
	 *
	 * @param enable True: Editable, False: Read-Only
	 */
	public void setEditable(boolean enable)
	{
		editable_ = enable;
	}

	/**
	 * Sets the outline model.
	 *
	 * @param outline The model.
	 */
	public void setOutlineModel(OutlineModel outline)
	{
		this.outlineModel_ = outline;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}

	@Override
	public int getColumnCount()
	{
		return PropertyTable.COLUMN_COUNT;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return "";
	}

	@Override
	public int getRowCount()
	{
		return -1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		// Always return the node, the cell-renderer/-editor
		// access always through the node.
		return getNodeForRow(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		DefaultMutableTreeNode node = getNodeForRow(rowIndex);
		if (node instanceof PropertyGroupNode)
		{
			return false;
		}
		else
		{
			return editable_;
		}
	}

	@Override
	public synchronized void removeTableModelListener(TableModelListener l)
	{
		listeners_.remove(l);
	}

	@Override
	public synchronized void addTableModelListener(TableModelListener l)
	{
		listeners_.add(l);
	}

	private void fire(TableModelEvent e)
	{
		TableModelListener[] l;
		synchronized (this)
		{
			l = new TableModelListener[listeners_.size()];
			l = listeners_.toArray(l);
		}
		for (TableModelListener tableModelListener : l)
		{
			tableModelListener.tableChanged(e);
		}
	}

	/**
	 * Used by CellEditor.
	 *
	 * @param rowIndex    The model index of the row.
	 * @param columnIndex The model index of the column.
	 */
	public void fireTableChanged(int rowIndex, int columnIndex)
	{
		TableModelEvent e = new TableModelEvent(this, rowIndex, rowIndex, columnIndex);
		fire(e);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		// Not used, Data is maintained directly via Nodes.
	}

	private DefaultMutableTreeNode getNodeForRow(int row)
	{
		return (DefaultMutableTreeNode) outlineModel_.getValueAt(row, 0);
	}
}
