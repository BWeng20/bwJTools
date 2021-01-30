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
package com.bw.jtools.ui.fontchooser;

import javax.swing.*;
import java.awt.*;

/**
 * A List Cell renderer that renders Font items.
 */
public class FontCellRenderer implements ListCellRenderer<Font>
{
    private final JLabel label_;

    public FontCellRenderer()
    {
        DefaultListCellRenderer d;
        label_ = new JLabel();
        label_.setOpaque(true);
        label_.setBorder( BorderFactory.createEmptyBorder(2,3,3,0));
    }

    protected Font getDefaultFont()
    {
        return javax.swing.UIManager.getDefaults().getFont("Label.font");
    }


    @Override
    public Component getListCellRendererComponent(JList list, Font font, int index, boolean isSelected, boolean cellHasFocus)
    {
        Color cellForeground = null;
        Color cellBackground = null;

        if ( font == null )
        {
            label_.setFont( getDefaultFont() );
            label_.setText("");
        }
        else
        {
            String txt = font.getFamily();

            if ( font.canDisplayUpTo(txt) == -1)
                label_.setFont(font);
            else
                label_.setFont(getDefaultFont());
            label_.setText( txt );
        }

        if (isSelected)
        {
            cellForeground = list.getSelectionForeground();
            cellBackground = list.getSelectionBackground();

            if ( cellForeground == null ) cellForeground = Color.WHITE;
            if ( cellBackground == null ) cellBackground = Color.BLACK;
        }
        else
        {
            cellBackground = list.getBackground();
            cellForeground = list.getForeground();

            if ( cellForeground == null ) cellForeground = Color.BLACK;
            if ( cellBackground == null ) cellBackground = Color.WHITE;
        }

        // Specially for Nimbus the color-derivatives seems not to work if set as color directly.
        // (hmmm, perhaps some over-engineering?)
        // Copy them solves the issue.
        label_.setForeground(new Color( cellForeground.getRGB() ));
        label_.setBackground(new Color( cellBackground.getRGB() ));

        return label_;
    }
}
