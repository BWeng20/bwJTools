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
package com.bw.jtools.examples.applicationicons;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.SettingsUI;

import java.awt.*;
import javax.swing.*;

/**
 * Demonstration of Application-Icons.
 */
public class ApplicationIconsDemo
{
    static JFrame frame;

    static public void main( String args[] )
    {
        // Initialize library.
        Application.initialize(ApplicationIconsDemo.class);

        // The library has now initialized itself from the defaultsettings.properties.
        // parallel to the main-class.

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        frame = new JFrame("Application Icons Demo");
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(
                "<html><body><b>"+
                "Check icons:<br><ul>"+
                "<li>in the title bar</li>"+
                "<li>in OS status line</li>"+
                "<li><i>in windows</i>: holding Alt-Tab.</li></ul><br>"+
                "Java runtime will choose one of the<br>"+
                "provided icons, depending on the platform."+
                "</b></body></html>" );
        label.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        label.setPreferredSize(new Dimension(420, 150));
        panel.add( label, BorderLayout.CENTER );

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setIconImages( IconTool.getAppIconImages() );

        frame.setContentPane(panel);
        frame.pack();

        // Restore window-position and dimension from prefences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose( frame );
        frame.setVisible(true);

        Log.info("Started");

    }
}
