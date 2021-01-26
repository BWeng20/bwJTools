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

import com.bw.jtools.properties.PropertyColorValue;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JColorIcon;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Custom Cell Editor to support different value types in one column. Detection
 * of the value-type is retrieved from PropertyNode.valueClazz_.
 * <ul>
 * <li>Enum-types: A combo-box with all possible values is shown.
 * <li>Number-types: A text-field with local dependent number format is shown.
 * <li>Color: A colorized icon and the RGB-value is shown. A click opens the jdk
 * color-chooser.
 * <li>Boolean: A combo-box with "true", "false" and an empty entry is shown.
 * </ul>
 */
public class PropertyCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 2666866100515133280L;

	protected final PropertyEditorComponents components_ = new PropertyEditorComponents();

	protected final PropertyTable table_;


	public PropertyCellEditor(PropertyTable table)
	{
		table_ = table;
	}

	@Override
	public Object getCellEditorValue()
	{
		// We use this call only to handle the update after a stopEdit,
		components_.updateCurrentValue();
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		Component comp = null;

		if (table_.isCellEditable(row, column))
		{
			column = table_.convertColumnIndexToModel(column);
			row = table_.convertRowIndexToModel(row);

			PropertyNode pv = (PropertyNode) table_.getModel().getValueAt(row, -1);

			switch (column)
			{
			case PropertyTable.COLUMN_VALUE + 1:
				comp = components_.getEditorComponent(pv.property_);
			default:
				break;
			}

		}
		return comp;
	}

}
