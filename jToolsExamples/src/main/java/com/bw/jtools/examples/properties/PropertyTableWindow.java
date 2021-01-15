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
package com.bw.jtools.examples.properties;

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
    long changes_ = 0;
    long updates_ = 0;
    long propChanged_ = 0;
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

        table.getModel().addTableModelListener(new TableModelListener()
        {
            @Override
            public void tableChanged(TableModelEvent ev)
            {
                ++changes_;
                if ( ev.getType() == TableModelEvent.UPDATE )
                    ++updates_;
                updateCounters();
            }
        });

        show(new JScrollPane(table));

    }

    @Override
    protected void propertyChanged(PropertyValue value)
    {
        ++propChanged_;
        updateCounters();
    }

    protected void updateCounters() {
        SwingUtilities.invokeLater( () ->
            status_.setText(
                    "#tableChanged "+ nf_.format(changes_)+" (Updates "+ nf_.format(updates_)+") / #propertyChanged "+propChanged_ )
        );

    }
}
