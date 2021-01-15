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
import com.bw.jtools.ui.properties.sheet.PropertyGroupSheet;

import javax.swing.*;
import java.util.List;

/**
 * Demonstration of the property-sheets.
 */
public class PropertySheetWindow extends PropertyDemoWindowBase
{

    public PropertySheetWindow(List<PropertyGroup> groups)
    {
        // Base class creates frame, adds propertychangelisterer etc.
        super("Property Sheet Demo", groups );

        JTabbedPane tabs = new JTabbedPane();
        for ( PropertyGroup pg : groups_)
        {
            PropertyGroupSheet sheet = new PropertyGroupSheet(pg);
            tabs.add(pg.displayName_, sheet);
        }

        show( tabs );
    }

    protected void propertyChanged( PropertyValue v )
    {
        SwingUtilities.invokeLater(() ->  status_.setText( "propertyChanged '"+v.displayName_+"' \u21C9 "+v.getPayload()) );
    }
}
