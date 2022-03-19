/*
 * Copyright Bernd Wengenroth
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
package com.bw.jtools.examples.properties;

import com.bw.jtools.Application;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.ui.properties.sheet.PropertyGroupCard;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Demonstration of the property-tiles.
 */
public class PropertyTileWindow extends PropertyDemoWindowBase
{
    public static final String[][] DESCRIPTION =
    {
            { "en", "Demonstrates usage of <b>com.bw.jtools.ui.properties.sheet.PropertyGroupCard</b>." },
            { "de", "Demonstriert die Verwendung von <b>com.bw.jtools.ui.properties.sheet.PropertyGroupCard</b>." }
    };

    JPanel tiles;
    boolean addTitle_;

    public PropertyTileWindow(List<PropertyGroup> groups)
    {
        this( groups, true );
    }

    public PropertyTileWindow(List<PropertyGroup> groups, boolean addTitle)
    {
        // Base class creates frame, adds propertychangelisterer etc.
        super("Property Tile Demo", groups );

        addTitle_ = addTitle;

        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEADING);
        tiles = new JPanel( fl );
        tiles.setBorder( BorderFactory.createEmptyBorder(20,20,20,20));

        for ( PropertyGroup pg : groups_)
        {
            PropertyGroupCard sheet = new PropertyGroupCard(pg, addTitle);
            sheet.setBackground(Color.WHITE);
            tiles.add(sheet);
        }

        show( tiles );
    }

    public static void main(String[] args)
    {
        Application.initialize( PropertyTileWindow.class );
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        { e.printStackTrace();}

        List<PropertyGroup> groups = createGroups();
        PropertyTileWindow sheet = new PropertyTileWindow(groups);
    }

}
