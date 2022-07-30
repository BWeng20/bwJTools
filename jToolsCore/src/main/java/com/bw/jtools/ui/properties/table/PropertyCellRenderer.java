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

import com.bw.jtools.properties.PropertyFontValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.icon.JPaintIcon;
import org.netbeans.swing.outline.Outline;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Objects;

/**
 * Custom Cell Renderer to support different value types in the same column.
 * See @PropertyCellEditor for supported types.
 */
public class PropertyCellRenderer implements TableCellRenderer
{

	protected final Border noBorder_;
	protected final JLabel text_;
	protected final JLabel fontLabel_;
	protected final JLabel emptyLabel_;
	protected final Font font_;
	protected final Icon closed;
	protected final Icon open;
	protected final Icon empty;
	protected final JLabel groupHandle_;
	protected final Color groupBackground_;
	protected final JCheckBox booleanBox_;

	protected final JLabel paint_;
	protected final JPaintIcon paintIcon_;


	protected final NumberFormat nf_;

	public PropertyCellRenderer(PropertyTable table)
	{
		table_ = table;
		font_ = new java.awt.Font("SansSerif", Font.PLAIN, 11);

		noBorder_ = BorderFactory.createEmptyBorder(0, 5, 0, 0);

		emptyLabel_ = new JLabel("");
		emptyLabel_.setOpaque(true);

		text_ = new JLabel();
		text_.setOpaque(true);
		text_.setFont(font_);

		groupHandle_ = new JLabel();
		groupHandle_.setOpaque(true);
		groupHandle_.setFont(font_);

		booleanBox_ = new JCheckBox();
		booleanBox_.setOpaque(true);
		booleanBox_.setFont(font_);

		paintIcon_ = new JPaintIcon(13, 13, null);

		paint_ = new JLabel();
		paint_.setIcon(paintIcon_);
		paint_.setOpaque(true);
		paint_.setFont(font_);

		fontLabel_ = new JLabel();
		fontLabel_.setOpaque(false);
		fontLabel_.setFont(font_);

		closed = IconTool.getIcon(PropertyTable.class, "group_closed.png");
		open = IconTool.getIcon(PropertyTable.class, "group_open.png");

		// An empty icon for leafs, only to ensure all items are aligned.
		empty = IconTool.getIcon(PropertyTable.class, "group_empty.png");

		nf_ = NumberFormat.getInstance();

		groupBackground_ = new Color(200, 200, 200);
	}

	private final PropertyTable table_;


	/**
	 * Gets different widgets to handle the different data-types.
	 * PropertyGroupNode are shown with an expand-icons and different background.
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		column = table.convertColumnIndexToModel(column);
		if (table instanceof Outline)
			--column;

		JComponent comp = null;
		Color cellForeground;
		Color cellBackground;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		boolean group = (value instanceof PropertyGroupNode);

		if (value != null && column == -1)
		{
			AbstractLayoutCache lc = table_.getLayoutCache();
			boolean expanded = lc.isExpanded(lc.getPathForRow(row));

			if (group)
				groupHandle_.setIcon(expanded ? open : closed);
			else
				groupHandle_.setIcon(empty);

			groupHandle_.setText(group
					? ((PropertyGroupNode) node).group_.displayName_
					: ((PropertyNode) node).property_.displayName_);

			comp = groupHandle_;

		}
		else if (column == PropertyTable.COLUMN_VALUE)
		{
			if (!group && node != null)
			{
				PropertyValue<Object> propVal = ((PropertyNode) node).property_;

				final Object val = propVal.getValue();

				if (propVal.possibleValues_ != null)
				{
					String key = null;
					for (Map.Entry<String, Object> entry : propVal.possibleValues_.entrySet())
					{
						if (Objects.equals(entry.getValue(), val))
						{
							key = entry.getKey();
						}
					}
					text_.setText(key == null ? "" : key);
					comp = text_;
				}
				else if (val instanceof Number)
				{
					text_.setText((propVal.nf_ == null ? nf_ : propVal.nf_).format(val));
					comp = text_;
				}
				else if (Paint.class.isAssignableFrom(propVal.valueClazz_))
				{
					Paint c = (Paint) val;
					if (c == null) c = Color.BLACK;
					paintIcon_.setPaint(c);
					paint_.setText(UITool.paintToString(c));
					comp = paint_;
				}
				else if (Font.class.isAssignableFrom(propVal.valueClazz_))
				{
					Font f = (Font) val;
					fontLabel_.setText(PropertyFontValue.toString(f));
					fontLabel_.setFont(f == null ? font_ : f);
					comp = fontLabel_;
				}
				else if ((propVal.valueClazz_ == Boolean.class) && !propVal.nullable_)
				{
					booleanBox_.setSelected(val != null && ((Boolean) val).booleanValue());
					comp = booleanBox_;
				}
				else
				{
					final String sval = val != null ? String.valueOf(val) : "";
					text_.setText(sval);
					comp = text_;
				}
			}
		}

		if (comp == null)
		{
			comp = emptyLabel_;
		}

		// Setting colors and border.
		// As this is look&feel depended, we have to use the UIManager to get the values.
		if (isSelected)
		{
			cellForeground = UIManager.getColor("Table.selectionForeground");
			cellBackground = UIManager.getColor("Table.selectionBackground");
		}
		else
		{
			if (group)
				cellBackground = groupBackground_;
			else
				cellBackground = UIManager.getColor("Table.background");

			cellForeground = UIManager.getColor("Table.foreground");
		}

		comp.setForeground(cellForeground);
		comp.setBackground(cellBackground);

		if (column == PropertyTable.COLUMN_VALUE)
		{
			comp.setBorder(noBorder_);
		}
		else
		{
			comp.setBorder(null);
		}
		return comp;

	}
}
