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
package com.bw.jtools.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import com.bw.jtools.Log;


/**
 *
 *
 */
public final class UIToolSwing
{
    // Execute code inside the UI Thread (enables us to switch to other UI system, e.g. JavFX / Swing /AWT)
    public static void executeInUIThread( Runnable r )
    {
       SwingUtilities.invokeLater(r);
    }


    public static void setDoubledBuffered( Container w, boolean enable)
    {
        for ( Component c : w.getComponents() )
            if ( c instanceof Container )
                setDoubledBuffered((Container)c, enable);
        if ( w instanceof JComponent )
            ((JComponent)w).setDoubleBuffered(enable);

    }

    private static final Icon dummyIcon = new DummyIcon();


    public static Icon createButtonIcon( BufferedImage img )
    {
        return  createButtonIcon( img, 20 );
    }

    public static Icon createButtonIcon( BufferedImage img, int targetHeight )
    {
        Icon ic;
        if ( img != null )
        {
            try
            {
               int calcWidth = (int)(0.5 + ((double)(targetHeight*img.getWidth()))/img.getHeight());
               ic = new ImageIcon( img.getScaledInstance(calcWidth,targetHeight, Image.SCALE_SMOOTH) );
            }
            catch ( Throwable t)
            {
                Log.error(t.getMessage(), t);
                ic = dummyIcon;
            }
        }
        else
            ic = null;
        return ic;
    }


    public static JButton createIconButton( String icon )
    {
        JButton b = new JButton(IconCache.getIcon(icon));
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(false);
        return b;
    }

    public static JButton createIconRolloverButton( String iconPrefix )
    {
        JButton b = createIconButton( iconPrefix+".png" );

        b.setRolloverEnabled(true);
        Icon roIcon = IconCache.getIcon( iconPrefix+"_ro.png");
        b.setRolloverIcon(roIcon);
        b.setPressedIcon(roIcon);
        return b;
    }

}
