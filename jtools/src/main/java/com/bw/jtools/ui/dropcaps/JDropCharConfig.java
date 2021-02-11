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
package com.bw.jtools.ui.dropcaps;

import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JIconButton;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.fontchooser.JFontChooser;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.icon.JColorIcon;
import com.bw.jtools.ui.image.MaskComposite;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class JDropCharConfig extends JPanel
{
    JDropCapsLabel dropCap_;
    JColorIcon textColorIcon_;
    JColorIcon dropCapColorIcon_;

    public JDropCharConfig(JDropCapsLabel dropCap)
    {
        dropCap_ = dropCap;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // Because this dialog should work as tool window, we use minimal icon buttons instead the
        // specialized font/color buttons.

        Icon fontIcon = IconTool.getIcon( JDropCharConfig.class,"font.png" );
        JIconButton font = new JIconButton( fontIcon );
        font.addActionListener(e -> {
            Font f = JFontChooser.showDialog(this,
                    I18N.getText("dropcapconfig.fontDialogTitle"),
                    dropCap_.getFont());
            if ( f != null )
            {
                dropCap_.setFont(f);
            }
        });
        add(font);
        add(Box.createHorizontalStrut(5));

        // Add a color-button that use a color-chooser to select the text-color.
        // Adapt icon size, but subtract 2 Pixel to adjust optical as the font icon is AA.
        textColorIcon_ = new JColorIcon(fontIcon.getIconWidth()-2, fontIcon.getIconHeight()-2, dropCap_.getDropCapPaint());
        JIconButton textColor = new JIconButton(textColorIcon_);
        textColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,
                    I18N.getText("dropcapconfig.colorDialogTitle"),
                    dropCap_.getForeground());
            if ( c != null )
            {
                textColorIcon_.setColor(c);
                dropCap_.setForegroundPaint(c);
            }
        });
        add(textColor);
        add(Box.createHorizontalStrut(5));


        // Add a color-button to select the drop-caps-color.
        dropCapColorIcon_ = new JColorIcon(fontIcon.getIconWidth()-2, fontIcon.getIconHeight()-2, dropCap_.getDropCapColor());
        JIconButton dropCapColor = new JIconButton(dropCapColorIcon_);
        dropCapColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,
                    I18N.getText("dropcapconfig.colorDialogTitle"),
                    dropCap_.getDropCapColor());
            if ( c != null )
            {
                dropCapColorIcon_.setColor(c);
                dropCap_.setDropCapColor(c);
            }
        });
        add(dropCapColor);
        add(Box.createHorizontalStrut(5));

        JComboBox<String> dropCapModes = new JComboBox<>();
        dropCapModes.setBackground(Color.WHITE);
        dropCapModes.setOpaque(true);
        LinkedHashMap<String, Composite> map = new LinkedHashMap<>();
        map.put( "None", null );
        map.put( "MaskAlpha", new MaskComposite() );
        map.put( "Clear", AlphaComposite.Clear);
        map.put( "Xor", AlphaComposite.Xor);
        map.put( "Dst", AlphaComposite.Dst);
        map.put( "DstAtop", AlphaComposite.DstAtop);
        map.put( "DstIn", AlphaComposite.DstIn);
        map.put( "DstOut", AlphaComposite.DstOut);
        map.put( "SrcAtop", AlphaComposite.SrcAtop);
        map.put( "DstOver", AlphaComposite.DstOver);
        map.put( "Src", AlphaComposite.Src);
        map.put( "SrcIn", AlphaComposite.SrcIn);
        map.put( "SrcOut", AlphaComposite.SrcOut);

        for ( String m : map.keySet())
            dropCapModes.addItem( m );

        dropCapModes.addItemListener(e ->
        {
            String c = (String)dropCapModes.getSelectedItem();
            if ( c != null )
            {
                dropCap_.setDropCapPaint(dropCap_.getDropCapPaint(), map.get(c));
            }
        });

        add(dropCapModes);
    }

    public static void showDialog( JDropCapsLabel dropCap, String title )
    {
        // Initialization of font list may take several seconds.
        // Show a wait-cursor.
        Window w = SwingUtilities.getWindowAncestor(dropCap);

        JDropCharConfig chooserPane = new JDropCharConfig(dropCap);
        JDialog chooserDialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);

        chooserDialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        JPanel c = new JPanel();
        chooserDialog.setContentPane(c);
        c.setLayout(new BorderLayout());
        c.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        c.add(chooserPane, BorderLayout.CENTER);

        chooserDialog.pack();

        UITool.placeAtSide( dropCap, chooserDialog );

        chooserDialog.setVisible(true);
    }
}
