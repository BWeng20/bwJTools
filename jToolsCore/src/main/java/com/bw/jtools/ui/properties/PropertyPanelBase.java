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

import com.bw.jtools.properties.PropertyChangeClient;
import com.bw.jtools.properties.PropertyChangeListener;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Helper base class for property panels.
 */
public class PropertyPanelBase extends JPanel
{

    protected PropertyTable table_ = new PropertyTable();
    protected PropertyChangeClient propClient_ = new PropertyChangeClient();

    public PropertyPanelBase()
    {
        super(new BorderLayout());
        add(table_, BorderLayout.CENTER);
    }

    protected <T> void addProperty(PropertyGroup group, PropertyValue<T> value, PropertyChangeListener<T> pcl)
    {
        value.nullable_ = false;
        propClient_.addProperty(group, value, pcl);
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();

        int h = table_.getTableModel()
                .getRowCount() * table_.getRowHeight();
        int w = table_.getFontMetrics(table_.getFont())
                .charWidth('A') * 50;

        return new Dimension(Math.max(w, d.width), Math.max(h, d.height));
    }
}
