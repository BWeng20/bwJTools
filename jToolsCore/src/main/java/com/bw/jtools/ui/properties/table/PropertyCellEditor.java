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

import com.bw.jtools.ui.properties.PropertyEditorComponents;
import com.bw.jtools.ui.properties.valuetypehandler.ValueTypeHandler;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import java.awt.Component;

/**
 * Custom Cell Editor to support different value types in one column. The value-type is
 * detected from PropertyNode.valueClazz_.
 * <ul>
 * <li>Enum-types: A combo-box with all possible values is used.
 * <li>Number-types: A text-field with local dependent number format is used.
 * <li>Color: A colorized icon and the RGB-value is used. A click opens the jdk
 * color-chooser.
 * <li>Boolean: If nullable, a combo-box with "true", "false" and an empty entry is used.
 *              If not nullable a checkbox is used.
 * </ul>
 */
public class PropertyCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 2666866100515133280L;

	protected final PropertyEditorComponents components_ = new PropertyEditorComponents();
	protected ValueTypeHandler currentHandler_ = null;
	protected final PropertyTable table_;


	public PropertyCellEditor(PropertyTable table)
	{
		table_ = table;
	}

	@Override
	public Object getCellEditorValue()
	{
		// We use this call only to handle the update after a stopEdit,
		if ( currentHandler_ != null )
			currentHandler_.updatePropertyFromEditor();
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		currentHandler_ = null;

		if (table_.isCellEditable(row, column))
		{
			column = table_.convertColumnIndexToModel(column);
			row = table_.convertRowIndexToModel(row);

			PropertyNode pv = (PropertyNode) table_.getModel()
												   .getValueAt(row, -1);

			switch (column)
			{
				case PropertyTable.COLUMN_VALUE + 1:
					currentHandler_ = components_.getHandler(pv.property_, false);
				default:
					break;
			}

		}
		return currentHandler_ == null ? null : currentHandler_.getComponent();
	}

}
