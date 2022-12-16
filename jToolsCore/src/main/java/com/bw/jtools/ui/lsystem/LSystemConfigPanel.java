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
package com.bw.jtools.ui.lsystem;

import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.ui.properties.PropertyPanelBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.swing.tree.DefaultTreeModel;

public class LSystemConfigPanel extends PropertyPanelBase
{
    private LSystemPanel lSystemPanel_;

    public LSystemConfigPanel(LSystemPanel lpanel)
    {
        this.lSystemPanel_ = lpanel;
        init();
    }

    protected void init()
    {
        DefaultTreeModel model = table_.getTreeModel();

        PropertyGroup p = new PropertyGroup("Visual Settings");

        addProperty(p, new PropertyBooleanValue("Border", lSystemPanel_.isDrawBorder()), value ->
        {
            Boolean n = value.getValue();
            lSystemPanel_.setDrawBorder(n);
        });

        addProperty(p, new PropertyNumberValue("Angel (degree)",
                Math.toDegrees(lSystemPanel_.getLSystem().getAngle())), value ->
        {
            double d = Math.toRadians(value.getValue().doubleValue());
            lSystemPanel_.getLSystem().setAngle(d);
            lSystemPanel_.updateLSystem();
        });

        addProperty(p, new PropertyNumberValue("Delta X", lSystemPanel_.getLSystem().getDelta().getX()), value ->
        {
            double d = value.getValue().doubleValue();
            lSystemPanel_.getLSystem().setDelta(d, lSystemPanel_.getLSystem().getDelta().getY());
            lSystemPanel_.updateLSystem();
        });

        addProperty(p, new PropertyNumberValue("Delta Y", lSystemPanel_.getLSystem().getDelta().getY()), value ->
        {
            double d = value.getValue().doubleValue();
            lSystemPanel_.getLSystem().setDelta(lSystemPanel_.getLSystem().getDelta().getX(), d);
            lSystemPanel_.updateLSystem();
        });

        PropertyGroupNode root = new PropertyGroupNode(null);
        root.add(new PropertyGroupNode(p));

        model.setRoot(root);
        table_.expandAll();
    }

}
