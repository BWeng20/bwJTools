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
import com.bw.jtools.ui.JCardBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A convenience wrapper for a property sheet with a card-border and (optionally) a title.<br>
 */
public class PropertyGroupCard extends JPanel
{
    /** Card Border */
    protected static JCardBorder cardBorder_ = new JCardBorder(0.997f);

    /** Border for content area in case a title is shown. */
    protected static Border contentBorder_ = BorderFactory.createEmptyBorder(10,5,0,0);

    /** The label for the title if needed, otherwise null. */
    protected JLabel title_;

    /** The inner property sheet. */
    protected PropertyGroupSheet sheet_;

    /**
     * Creates a property card for a property-group.
     * @param group The group of properties.
     * @param addTitle If true the name of the group is shown as title.
     */
    public PropertyGroupCard(PropertyGroup group, boolean addTitle )
    {
        super( new BorderLayout() );

        sheet_ = new PropertyGroupSheet(group);
        sheet_.setBackground(null);

        Border outerBorder = BorderFactory.createCompoundBorder( cardBorder_, BorderFactory.createEmptyBorder(10,10,10,10) );

        setBorder(outerBorder);
        if (addTitle)
        {
            sheet_.setBorder(contentBorder_);
            title_ = new JLabel(group.displayName_, JLabel.LEADING);
            Font f = javax.swing.UIManager.getDefaults().getFont("Label.font");
            f = f.deriveFont(f.getSize()*1.5f);
            title_.setFont(f);

            add(title_, BorderLayout.NORTH);
        }
        add(sheet_, BorderLayout.CENTER);
    }

    @Override
    public void updateUI()
    {
        super.updateUI();
        if ( title_ != null)
        {
            Font f = javax.swing.UIManager.getDefaults().getFont("Label.font");
            f = f.deriveFont(f.getSize() * 1.5f);
            title_.setFont(f);
        }
    }
}
