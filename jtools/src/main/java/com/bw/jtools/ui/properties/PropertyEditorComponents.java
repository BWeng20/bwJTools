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
package com.bw.jtools.ui.properties;

import com.bw.jtools.properties.*;
import com.bw.jtools.ui.*;
import com.bw.jtools.ui.properties.table.PropertyNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

/**
 * Factory to create and configure editor-components for property-values.<br>
 * Holds one instance for each property type.<br>
 * <ul>
 * <li>Enum-types: A combo-box with all possible values is shown.
 * <li>Number-types: A text-field with local dependent number format is shown.
 * <li>Color: A colorized icon and the RGB-value is shown. A click opens the jdk
 * color-chooser.
 * <li>Boolean: A combo-box with "true", "false" and an empty entry is shown.
 * </ul>
 */
public class PropertyEditorComponents
{
	private static NumberFormat nf_;
	private static Border empty_border_ =  BorderFactory.createEmptyBorder();
	private static Font font_ = new Font("SansSerif", Font.PLAIN, 11);

	private JComboBox<String> choice_;
	private JComboBox<Object> enums_;
	private JComboBox<Boolean> booleanNullable_;
	private JCheckBox booleanCheckbox_;
	private ItemListener itemListener_;
	private JTextField text_;
	private JFontButton fontb_;
	private JButton color_;
	private JColorIcon colorIcon_;
	private PropertyValue currentValue_;

	static
	{
		nf_ = NumberFormat.getInstance();
		nf_.setGroupingUsed(false);

	}

	private Border getEmptyBorder()
	{
		return empty_border_;
	}

	private JTextField getTextField()
	{
		if ( text_ == null )
		{
			text_ = new JTextField();
			text_.setFont(font_);
			text_.addActionListener( (actionEvent) ->
				{
					updateCurrentValue();
				}
			);
		}
		return text_;
	}


	private ItemListener getItemListener() {
		if ( itemListener_ == null )
		{
			itemListener_ = (evt) ->
			{
				updateCurrentValue();
			};
		}
		return itemListener_;
	};

	private JComboBox getEnumCombo()
	{
		if ( enums_ == null)
		{
			enums_ = new JComboBox<>();
			enums_.setFont(font_);
			// Force UI to act as Cell editor (mainly to use a different selection/focus
			// handling)
			enums_.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
			enums_.addItemListener(getItemListener());

		}
		return enums_;
	}

	private JComboBox<String> getChoiceCombo()
	{
		if ( choice_ == null)
		{
			choice_ = new JComboBox<>();
			choice_.setFont(font_);
			// Force UI to act as Cell editor (mainly to use a different selection/focus
			// handling)
			choice_.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
			choice_.addItemListener(getItemListener());
		}
		return choice_;
	}


	private JComboBox getBooleanNullableCombo()
	{
		if ( booleanNullable_ == null)
		{
			booleanNullable_ = new JComboBox<>();
			booleanNullable_.addItem(null);
			booleanNullable_.addItem(Boolean.TRUE);
			booleanNullable_.addItem(Boolean.FALSE);
			booleanNullable_.setFont(font_);
			booleanNullable_.addItemListener(getItemListener());
		}
		return booleanNullable_;
	}

	private JCheckBox getBooleanCheckbox()
	{
		if ( booleanCheckbox_ == null )
		{
			booleanCheckbox_ = new JCheckBox();
			booleanCheckbox_.setFont(font_);
			booleanCheckbox_.setOpaque(false);
			booleanCheckbox_.addItemListener(getItemListener());
		}
		return booleanCheckbox_;
	}

	private JColorIcon getColorIcon()
	{
		if ( colorIcon_ == null )
		{
			colorIcon_ = new JColorIcon(13, 13, null);
		}
		return colorIcon_;
	}

	private JFontButton getFontButton()
	{
		if ( fontb_ == null )
		{
			fontb_ = new JFontButton();
			fontb_.setFont(font_);
			fontb_.addActionListener(ev -> {
				Font f = JFontChooser.showDialog(fontb_, "Select Font", fontb_.getSelectedFont() );
				if ( f != null )
				{
					fontb_.setSelectedFont(f);
				}
			});
		}
		return fontb_;
	}

	private JButton getColorButton()
	{
		if ( color_ == null )
		{
			color_ = new JButton();
			color_.setIcon(getColorIcon());
			color_.setOpaque(false);
			color_.setBorderPainted(false);
			color_.setMargin(new Insets(0, 0, 0, 0));
			color_.setHorizontalAlignment(JButton.LEFT);
			color_.setFont(font_);

			color_.addActionListener( (ae) ->
				{
					Color newColor = JColorChooser.showDialog(color_,
							I18N.getText("propertytable.colorchooser.title"),
							colorIcon_.getColor());
					if (!Objects.equals(colorIcon_.getColor(), newColor))
					{
						colorIcon_.setColor(newColor);
						color_.setText(PropertyColorValue.toString(newColor));
						if (currentValue_ != null && Color.class.isAssignableFrom(currentValue_.valueClazz_))
						{
							currentValue_.setPayload(newColor);
						}
					}
				}
			);
		}
		return color_;
	}

	/**
	 * Returns a configured component for the value type.<br>
	 * @param value The property value.
	 * @return The configured component.
	 */
	public Component getEditorComponent(PropertyValue value)
	{
		currentValue_ = value;

		boolean useGenericText = false;
		JComponent ed = null;

		if ( value.possibleValues_ != null )
		{
			JComboBox choice = getChoiceCombo();
			ed = choice;
			choice.removeAllItems();

			Object v = value.getPayload();
			if (value.nullable_)
			{
				choice.addItem(null);
				if ( v == null )
					choice.setSelectedIndex(0);
			}

			for (Map.Entry<String, Object> entry : value.possibleValues_.entrySet())
			{
				choice.addItem(entry.getKey());
				if ( Objects.equals( entry.getValue(), v))
				{
					choice.setSelectedIndex( choice.getItemCount()-1 );
				}
			}
			ed = choice;
		}
		else if (value.valueClazz_ == String.class)
		{
			useGenericText = true;
		}
		else if (Number.class.isAssignableFrom(value.valueClazz_))
		{
			JTextComponent text = getTextField();
			ed = text;
			Number i = (Number) value.getPayload();
			if (i != null)
			{
				NumberFormat nf = (value.nf_ == null ? nf_ : value.nf_);
				boolean gu = nf.isGroupingUsed();
				if ( gu ) nf.setGroupingUsed(false);
				text.setText(nf.format(i));
				if ( gu ) nf.setGroupingUsed(true);
			}
			else
				text.setText("");
		}
		else if (value.valueClazz_ == Boolean.class)
		{
			Boolean val = (Boolean) value.getPayload();
			if (value.nullable_)
			{
				JComboBox booleanNullable = getBooleanNullableCombo();
				booleanNullable.setSelectedItem(val);
				ed = booleanNullable;
			} else
			{
				JCheckBox booleanCheckbox = getBooleanCheckbox();
				booleanCheckbox.setSelected(val != null && val.booleanValue());
				ed = booleanCheckbox;

			}
		}
		else if (value.valueClazz_.isEnum())
		{
			JComboBox<Object> enums = getEnumCombo();
			enums.removeAllItems();
			if (value.nullable_)
				enums.addItem(null);

			Object vals[] = value.valueClazz_.getEnumConstants();
			for (Object v : vals)
				enums.addItem(v);
			enums.setSelectedItem(value.getPayload());
			ed = enums;
		}
		else if (Color.class.isAssignableFrom(value.valueClazz_))
		{
			Color c = (Color) value.getPayload();
			if (c == null) c = Color.BLACK;

			JButton color = getColorButton();
			color.setText(PropertyColorValue.toString(c));
			getColorIcon().setColor(c);
			ed = color;
		}
		else if (Font.class.isAssignableFrom(value.valueClazz_))
		{
			JFontButton fontb = getFontButton();
			fontb.setSelectedFont((Font) value.getPayload());
			ed = fontb;
		}
		else
		{
			useGenericText = true;
		}
		if (useGenericText)
		{
			JTextComponent text = getTextField();
			ed = text;
			if (value.hasContent())
				text.setText(String.valueOf(value.getPayload()));
			else
				text.setText("");
		}

		return ed;
	}

	public Number getNumberValue(PropertyValue value)
	{
		try
		{
			NumberFormat nf = nf_;
			if (value != null && value.nf_ != null)
				nf = value.nf_;
			Number nb = nf.parse(getTextField().getText());
			return nb;

		} catch (ParseException e)
		{
		}
		return null;
	}

	public boolean updateCurrentValue()
	{
		if ( currentValue_ == null )
			return false;

		Object newUserObject = currentValue_.getPayload();

		if (currentValue_.possibleValues_ != null)
		{
			String v = (String)getChoiceCombo().getSelectedItem();
			newUserObject = v == null ? null : currentValue_.possibleValues_.get(v);
		}
		else if (currentValue_.valueClazz_ == String.class)
		{
			String text = getTextField().getText();
			if ( text.isEmpty() && currentValue_.nullable_)
				newUserObject = null;
			else
				newUserObject = text;
		}
		else if (Number.class.isAssignableFrom(currentValue_.valueClazz_))
		{
			Number nb = getNumberValue(currentValue_);
			if (nb != null)
			{
				newUserObject = currentValue_.scaleNumber(nb);
			} else if (currentValue_.nullable_)
			{
				newUserObject = null;
			}
		}
		else if (currentValue_.valueClazz_ == Boolean.class)
		{
			Boolean newBool;
			if (currentValue_.nullable_)
			{
				newBool = (Boolean) getBooleanNullableCombo().getSelectedItem();
			} else
			{
				newBool = getBooleanCheckbox().isSelected();
			}
			newUserObject = newBool;
		}
		else if (currentValue_.valueClazz_.isEnum())
		{
			newUserObject = getEnumCombo().getSelectedItem();
		}
		else if (Color.class.isAssignableFrom(currentValue_.valueClazz_))
		{
			newUserObject = getColorIcon().getColor();
		}
		else if (Font.class.isAssignableFrom(currentValue_.valueClazz_))
		{
			newUserObject = getFontButton().getSelectedFont();
		}

		boolean changed = false;
		if (newUserObject == null)
		{
			changed = currentValue_.getPayload() != null;
		} else
		{
			changed = !newUserObject.equals(currentValue_.getPayload());
		}
		currentValue_.setPayload(newUserObject);
		return changed;
	}
}
