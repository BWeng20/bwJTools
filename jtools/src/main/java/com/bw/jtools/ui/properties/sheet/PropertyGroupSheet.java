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
package com.bw.jtools.ui.properties.sheet;

import com.bw.jtools.properties.PropertyColorValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JColorIcon;
import com.bw.jtools.ui.properties.table.PropertyCellRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

/**
 * A Panel to show and edit properties of a group.<br>
 * To be used inside a JTabbedPane or with a border in some combined editor.
 */
public class PropertyGroupSheet extends JPanel
{
    protected PropertyGroup group_;

    /**
     * Creates a property sheet for a property-group.
     * @param group The group of properties.
     */
    public PropertyGroupSheet(PropertyGroup group )
    {
        super( new GridBagLayout());
        this.group_ = group;
        int N = group.size();
        for (int i=0 ; i<N ; ++i)
        {
            addProperty( group.getPropertyAt(i), i );
        }
    }

    /**
     * Get component for property.<br>
     * Thus method work similar to {@link com.bw.jtools.ui.properties.table.PropertyCellEditor#getEditorComponent(PropertyValue) PropertyCellEditor.getEditorComponent}
     */
    protected JComponent getComponent(PropertyValue value)
    {
        boolean useGenericText = false;
        JComponent ed = null;
        if (value.valueClazz_ == String.class || Number.class.isAssignableFrom(value.valueClazz_))
        {
            useGenericText = true;
        }
        else if (value.valueClazz_ == Boolean.class)
        {
            Boolean val = (Boolean) value.getPayload();
            if (value.nullable_)
            {
                JComboBox<Boolean> booleanNullable = new JComboBox<>();
                booleanNullable.addItem(null);
                booleanNullable.addItem(Boolean.TRUE);
                booleanNullable.addItem(Boolean.FALSE);
                booleanNullable.setSelectedItem(val);

                booleanNullable.addItemListener(e -> {
                    value.setPayload( booleanNullable.getSelectedItem() );
                });

                ed = booleanNullable;
            }
            else
            {
                JCheckBox booleanCheckbox = new JCheckBox();
                booleanCheckbox.setSelected(val != null && val.booleanValue());
                booleanCheckbox.addItemListener(e -> {
                    value.setPayload( booleanCheckbox.isSelected() );
                });
                ed = booleanCheckbox;
            }
        }
        else if (value.valueClazz_.isEnum())
        {
            final JComboBox<Object> enums = new JComboBox<>();
            if (value.nullable_)
                enums.addItem(null);

            Object vals[] = value.valueClazz_.getEnumConstants();
            for (Object v : vals)
                enums.addItem(v);
            enums.setSelectedItem(value.getPayload());

            enums.addItemListener(e -> {
                value.setPayload( enums.getSelectedItem() );
            });

            ed = enums;
        }
        else if (Color.class.isAssignableFrom(value.valueClazz_))
        {
            JColorIcon colorIcon_ = new JColorIcon(13, 13, null);
            JButton color_ = new JButton();
            color_.setIcon(colorIcon_);
            color_.setOpaque(true);
            color_.setBorderPainted(false);
            color_.setMargin(new Insets(0, 2, 0, 0));
            color_.setHorizontalAlignment(JButton.LEFT);
            color_.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    Color newColor = JColorChooser.showDialog(color_, I18N.getText("propertytable.colorchooser.title"),
                            colorIcon_.getColor());
                    if (newColor != null)
                    {
                        colorIcon_.setColor(newColor);
                        color_.setText(PropertyColorValue.toString(newColor));
                        value.setPayload(newColor);
                    }
                }
            });

            Color col = (Color) value.getPayload();
            if (col == null)
            {
                col = Color.WHITE;
            } else
            {
                color_.setText(PropertyColorValue.toString(col));
            }
            colorIcon_.setColor(col);
            ed = color_;
        }
        else
        {
            useGenericText = true;
        }
        if (useGenericText)
        {
            JPropertyTextField txt = new JPropertyTextField(value);
            ed = txt;
        }

        return ed;
    }

    private void addProperty(PropertyValue v, int idx )
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = idx;
        add( new JLabel(v.displayName_), gbc );
        gbc.weightx = 1;
        if ( idx == group_.size()-1 )
            gbc.weighty = 1;
        gbc.insets = new Insets(3,5,0,5);
        gbc.gridx = 1;

        JComponent component = getComponent( v );
        if ( component instanceof JTextComponent )
            gbc.fill = GridBagConstraints.HORIZONTAL;

        add( component, gbc );
    }
}
