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

import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyEditorComponents;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * A panel to show and edit properties of a group.<br>
 * To be used inside a JTabbedPane or with a border in some editor.
 * @see PropertyGroupCard
 */
public class PropertyGroupSheet extends JPanel
{
    protected static Insets labelInsets_ = new Insets(3,0,0,0);
    protected static Insets fieldInsets_ = new Insets(3,5,0,5);

    /**
     * Creates a property sheet for a property-group.
     * @param group The group of properties.
     */
    public PropertyGroupSheet(PropertyGroup group )
    {
        super( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.PAGE_END;
        add( new JLabel(""), gbc);


        int N = group.size();
        for (int i=0 ; i<N ; ++i)
        {
            addProperty( group.getPropertyAt(i), i );
        }
    }

    private void addProperty(PropertyValue v, int idx )
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = labelInsets_;
        gbc.gridx = 0;
        gbc.gridy = idx;
        JLabel label = new JLabel(v.displayName_);
        add( label, gbc );
        gbc.weightx = 1;
        gbc.insets = fieldInsets_;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        Component component = new PropertyEditorComponents().getEditorComponent(v);
        if ( component instanceof JTextComponent )
            gbc.fill = GridBagConstraints.HORIZONTAL;
        else
            gbc.fill = GridBagConstraints.NONE;

        add( component, gbc );
    }
}
