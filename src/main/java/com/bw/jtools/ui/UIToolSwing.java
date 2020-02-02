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
import javax.swing.JLabel;


/**
 * Collections of tools for using swing.
 */
public final class UIToolSwing
{
    /**
     * Execute code inside the UI Thread
     * @param r The runnable to execute.
     */
    public static void executeInUIThread( Runnable r )
    {
       SwingUtilities.invokeLater(r);
    }


    /**
     * Switch the double-buffered-attribute for
     * a component and all sub-components.
     * @param w The container to modify.
     * @param enable The new "double buffered" state.
     */
    public static void setDoubledBuffered( Container w, boolean enable)
    {
        for ( Component c : w.getComponents() )
            if ( c instanceof Container )
                setDoubledBuffered((Container)c, enable);
        if ( w instanceof JComponent )
            ((JComponent)w).setDoubleBuffered(enable);

    }

    private static final Icon dummyIcon = new DummyIcon();

    /**
     * Creates a swing icon from an image.
     * @see #createButtonIcon(java.awt.image.BufferedImage, int)
     * @param img The image to use.
     * @return The created icon.
     */
    public static Icon createButtonIcon( BufferedImage img )
    {
        return  createButtonIcon( img, 20 );
    }

    /**
     * Creates a swing icon from an image.
     * @param img The image to use.
     * @param targetHeight Height of the icon in pixel.
     * @return The created icon.
     */
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


    /**
     * Creates an icon-button.
     * @see IconCache#getIcon(java.lang.String)
     * @param icon The name of the icon to use.
     * @return The created button.
     */
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

    /**
     * Creates an icon-button.<br>
     * The application has to provide icons "icons/&lt;prefix&gt;.png" and
     * "icons/&lt;prefix&gt;_ro.png".
     * @see IconCache#getIcon(java.lang.String)
     * @param iconPrefix The name-prefix for the icons to use.
     * @return The created button.
     */
    public static JButton createIconRolloverButton( String iconPrefix )
    {
        JButton b = createIconButton( iconPrefix+".png" );

        b.setRolloverEnabled(true);
        Icon roIcon = IconCache.getIcon( iconPrefix+"_ro.png");
        b.setRolloverIcon(roIcon);
        b.setPressedIcon(roIcon);
        return b;
    }

    /**
     * Creates a text button with mnemonic.<br>
     * If a I18N key with the additional postfix ".mnemonic" exists, the first character of this text is used as mnemonic character.
     * @param i18NKey The I18N key for the button text.
     * @return The created button.
     * @see com.bw.jtools.ui.I18N#getText(java.lang.String)
     */
    public static JButton createI18NTextButton( String i18NKey )
    {
        JButton b = new JButton(  );
        setI18NText( b, i18NKey );
        return b;
    }

    /**
     * Sets button text and mnemonic.<br>
     * If a I18N key with the additional postfix ".mnemonic" exists, the first character of this text is used as mnemonic character.
     * @param i18NKey The I18N key for the button text.
     * @param button The button.
     * @see com.bw.jtools.ui.I18N#getText(java.lang.String)
     */
    public static void setI18NText( JButton button, String i18NKey )
    {
        button.setText(I18N.getText(i18NKey) );
        final String mnemonicKey = i18NKey+".mnemonic";
        if ( I18N.hasText( mnemonicKey ) )
        {
            button.setMnemonic( I18N.getText(mnemonicKey).charAt(0));
        }
        else if ( button.getMnemonic() != 0)
        {
            button.setMnemonic(0);
        }
    }

    /**
     * Creates a text label with mnemonic.<br>
     * If a I18N key with the additional postfix ".mnemonic" exists, the first character of this text is used as mnemonic character.
     * @param i18NKey The I18N key for the button text.
     * @return The created label.
     * @see com.bw.jtools.ui.I18N#getText(java.lang.String)
     */
    public static JLabel createI18NLabel( String i18NKey )
    {
        JLabel l = new JLabel( );
        setI18NText( l, i18NKey );
        return l;
    }

    /**
     * Sets text and mnemonic.<br>
     * If a I18N key with the additional postfix ".mnemonic" exists, the first character of this text is used as mnemonic character.
     * @param i18NKey The I18N key.
     * @param label The label.
     * @see com.bw.jtools.ui.I18N#getText(java.lang.String)
     */
    public static void setI18NText( JLabel label, String i18NKey )
    {
        label.setText(I18N.getText(i18NKey) );
        final String mnemonicKey = i18NKey+".mnemonic";
        if ( I18N.hasText( mnemonicKey ) )
        {
            label.setDisplayedMnemonic(I18N.getText(mnemonicKey).charAt(0));
        }
        else if ( label.getDisplayedMnemonic() != 0)
        {
            label.setDisplayedMnemonic(0);
        }
    }

}
