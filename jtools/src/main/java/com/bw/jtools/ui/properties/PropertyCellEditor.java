/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.ui.properties;

import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JColorIcon;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.text.ParseException;

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

	protected final Border empty_border_;

	protected final JComboBox<Object> enums_;
	protected final JComboBox<Boolean> booleanNullable_;
	protected final JCheckBox booleanCheckbox_;
	protected final JTextField text_;
	protected final JButton color_;
	protected final JColorIcon colorIcon_;
	protected final PropertyTable table_;
	protected final Font font_;

	protected int currentCol;
	protected int currentRow;
	protected PropertyValue currentNode_;

	protected NumberFormat nf_;

	public PropertyCellEditor(PropertyTable table)
	{
		font_ = new java.awt.Font("SansSerif", Font.PLAIN, 11);

		empty_border_ = BorderFactory.createEmptyBorder();
		table_ = table;

		text_ = new JTextField();
		text_.setFont(font_);
		text_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				triggerUpdate();
			}
		});

		ItemListener il = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				triggerUpdate();
			}
		};

		enums_ = new JComboBox<>();
		enums_.setFont(font_);
		// Force UI to act as Cell editor (mainly to use a different selection/focus
		// handling)
		enums_.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		enums_.addItemListener(il);

		booleanNullable_ = new JComboBox<>();
		booleanNullable_.addItem(null);
		booleanNullable_.addItem(Boolean.TRUE);
		booleanNullable_.addItem(Boolean.FALSE);
		booleanNullable_.setFont(font_);
		booleanNullable_.addItemListener(il);

		booleanCheckbox_ = new JCheckBox();
		booleanCheckbox_.setFont(font_);
		booleanCheckbox_.addItemListener(il);

		colorIcon_ = new JColorIcon(13, 13, null);

		color_ = new JButton();
		color_.setIcon(colorIcon_);
		color_.setOpaque(true);
		color_.setBorderPainted(false);
		color_.setMargin(new Insets(0, 2, 0, 0));
		color_.setHorizontalAlignment(JButton.LEFT);
		color_.setFont(font_);
		color_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				Color newColor = JColorChooser.showDialog(color_, I18N.getText("propertytable.colorchooser.title"),
				        colorIcon_.getColor());
				if (newColor != null)
				{
					colorIcon_.setColor(newColor);
					color_.setText(PropertyCellRenderer.toString(newColor));
					triggerUpdate();
				}
			}
		});

		nf_ = NumberFormat.getInstance();
	}

	@Override
	public Object getCellEditorValue()
	{
		// We use this call only to handle the update after a stopEdit,
		// triggered by base-functionality.
		triggerUpdate();
		return null;
	}

	protected void triggerUpdate()
	{
		if (updateCurrentValue())
		{
			table_.getTableModel().fireTableChanged(currentRow, currentCol);
		}
	}

	protected boolean updateCurrentValue()
	{
		if (currentNode_ == null)
			return false;

		Object newUserObject = currentNode_.getUserObject();

		if (currentNode_.valueClazz_ == String.class)
		{
			String text = text_.getText();

			if ((!text.isEmpty()) || !currentNode_.hasContent())
			{
				newUserObject = text;
			}
		} else if (Number.class.isAssignableFrom(currentNode_.valueClazz_))
		{
			Number nb = getNumberValue();
			if (nb != null)
			{
				Number newNb;
				if (currentNode_.valueClazz_ == Integer.class)
					newNb = nb.intValue();
				else if (currentNode_.valueClazz_ == Double.class)
					newNb = nb.doubleValue();
				else if (currentNode_.valueClazz_ == Long.class)
					newNb = nb.longValue();
				else if (currentNode_.valueClazz_ == Short.class)
					newNb = nb.shortValue();
				else if (currentNode_.valueClazz_ == Byte.class)
					newNb = nb.byteValue();
				else if (currentNode_.valueClazz_ == Float.class)
					newNb = nb.floatValue();
				else
					newNb = nb;

				newUserObject = newNb;
			} else if (currentNode_.nullable_)
			{
				newUserObject = null;
			}
		} else if (currentNode_.valueClazz_ == Boolean.class)
		{
			Boolean newBool;
			if (currentNode_.nullable_)
			{
				newBool = (Boolean) booleanNullable_.getSelectedItem();
			} else
			{
				newBool = booleanCheckbox_.isSelected();
			}
			newUserObject = newBool;
		} else if (currentNode_.valueClazz_.isEnum())
		{
			newUserObject = enums_.getSelectedItem();
		} else if (Color.class.isAssignableFrom(currentNode_.valueClazz_))
		{
			newUserObject = colorIcon_.getColor();
		}

		boolean changed = false;
		if (newUserObject == null)
		{
			changed = currentNode_.getUserObject() != null;
		} else
		{
			changed = !newUserObject.equals(currentNode_.getUserObject());
		}
		currentNode_.setUserObject(newUserObject);
		return changed;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		Component comp = null;

		if (table_.isCellEditable(row, column))
		{
			column = table_.convertColumnIndexToModel(column);
			row = table_.convertRowIndexToModel(row);

			PropertyValue pv = (PropertyValue) table_.getModel().getValueAt(row, -1);

			currentNode_ = null;
			currentCol = column;
			currentRow = row;

			switch (column)
			{
			case PropertyTable.COLUMN_VALUE + 1:
				comp = getEditorComponent(pv);
			default:
				break;
			}
			currentNode_ = pv;

		}
		return comp;
	}

	protected Component getEditorComponent(PropertyValue node)
	{
		boolean useGenericText = false;
		JComponent ed = null;
		if (node.valueClazz_ == String.class)
		{
			useGenericText = true;
		}
		if (Number.class.isAssignableFrom(node.valueClazz_))
		{
			ed = text_;
			Number i = (Number) node.getUserObject();
			if (i != null)
				text_.setText((node.nf_ == null ? nf_ : node.nf_).format(i));
			else
				text_.setText("");
		} else if (node.valueClazz_ == Boolean.class)
		{
			Boolean val = (Boolean) node.getUserObject();
			if (node.nullable_)
			{
				booleanNullable_.setSelectedItem(val);
				ed = booleanNullable_;
			} else
			{
				booleanCheckbox_.setSelected(val != null && val.booleanValue());
				ed = booleanCheckbox_;

			}
		} else if (node.valueClazz_.isEnum())
		{
			enums_.removeAllItems();
			if (node.nullable_)
				enums_.addItem(null);

			Object vals[] = node.valueClazz_.getEnumConstants();
			for (Object v : vals)
				enums_.addItem(v);
			enums_.setSelectedItem(node.getUserObject());
			ed = enums_;
		} else if (Color.class.isAssignableFrom(node.valueClazz_))
		{
			Color col = (Color) node.getUserObject();
			if (col == null)
			{
				col = Color.WHITE;
			} else
			{
				color_.setText(PropertyCellRenderer.toString(col));
			}

			colorIcon_.setColor(col);
			ed = color_;
		} else
		{
			useGenericText = true;
		}
		if (useGenericText)
		{
			ed = text_;
			if (node.hasContent())
				text_.setText(String.valueOf(node.getUserObject()));
			else
				text_.setText("");
		}

		return ed;
	}

	protected Number getNumberValue()
	{
		try
		{
			NumberFormat nf = nf_;
			if (currentNode_ != null && currentNode_.nf_ != null)
				nf = currentNode_.nf_;
			Number nb = nf.parse(text_.getText());
			return nb;

		} catch (ParseException e)
		{
		}
		return null;
	}

}
