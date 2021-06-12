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
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.text.NumberFormat;
import java.util.List;

/**
 * Demonstration of the property-table.
 */
public class PropertyTableWindow extends PropertyDemoWindowBase
{
    public static final String[][] DESCRIPTION =
    {
            { "en", "Demonstrates usage of <b>com.bw.jtools.ui.properties.table.PropertyTable</b>." },
            { "de", "Demonstriert die Verwendung von <b>com.bw.jtools.ui.properties.table.PropertyTable</b>." }
    };

    NumberFormat nf_ = NumberFormat.getInstance();

    public PropertyTableWindow(List<PropertyGroup> groups)
    {
        // Base class creates frame, adds propertychangelisterer etc.
        super("Property Table Demo", groups);

        PropertyGroupNode root = new PropertyGroupNode(null);
        for ( PropertyGroup pg : groups_)
        {
            root.add( new PropertyGroupNode( pg ) );
        }
        PropertyTable table = new PropertyTable();
        table.getTreeModel().setRoot(root);

        table.expandAll();
        // table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        show(new JScrollPane(table));

    }

    protected void propertyChanged( PropertyValue v )
    {
        super.propertyChanged(v);
        frame_.repaint();
    }

    public static void main(String[] args)
    {
        Application.initialize( PropertyTableWindow.class );
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        { e.printStackTrace();}

        List<PropertyGroup> groups = createGroups();
        PropertyTableWindow table = new PropertyTableWindow(groups);
    }

}
