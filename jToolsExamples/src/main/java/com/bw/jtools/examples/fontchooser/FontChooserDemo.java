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
package com.bw.jtools.examples.fontchooser;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.JFontButton;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;

public class FontChooserDemo
{

    public static void main(String[] args)
    {
        Log.setLevel(Log.DEBUG);
        // Initialize library.
        Application.initialize( FontChooserDemo.class );

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("FontChooser Demo");

        JPanel textPanel = new JPanel( new BorderLayout());
        JTextPane text = new JTextPane();
        text.setText("Hello World!");
        text.setPreferredSize(new Dimension(200,200));
        textPanel.add( new JScrollPane(text), BorderLayout.CENTER);

        JFontButton chooserButton = new JFontButton(text.getFont());

        chooserButton.addItemListener((evt) ->
        {
            Font f = (Font)evt.getItem();
            if ( f != null)
            {
                text.setFont(f);
            }
        } );

        JPanel status = new JPanel(new BorderLayout());
        JLAFComboBox lafCB = new JLAFComboBox();
        status.add(lafCB, BorderLayout.WEST );
        status.add( chooserButton, BorderLayout.EAST);

        JPanel mainPanel = new JPanel( new BorderLayout());
        mainPanel.add( textPanel, BorderLayout.CENTER);
        mainPanel.add( status, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages( IconTool.getAppIconImages() );
        frame.pack();

        // Restore window-position and dimension from preferences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose( frame );

        frame.setVisible(true);

        Log.info("Started");
    }

}
