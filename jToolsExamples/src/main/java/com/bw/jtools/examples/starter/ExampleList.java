/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.examples.starter;

import com.bw.jtools.ui.JExceptionDialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Main for Example Jar with Demo-selection.
 */
public final class ExampleList extends JFrame
{

    static String[][] examples =
    {
        { "Application Icons", "com.bw.jtools.examples.applicationicons.ApplicationIconsDemo" }  ,
        { "Data Table", "com.bw.jtools.examples.data.DataDemo" }  ,
        { "Exception Dialog", "com.bw.jtools.examples.exceptiondialog.JExceptionDialogDemo" }  ,
        { "Profiling Demo (console only)", "com.bw.jtools.examples.profiling.ProfilingDemo" }  ,
        { "Property Table", "com.bw.jtools.examples.propertytable.PropertyTableDemo" }  ,
        { "Tab Component", "com.bw.jtools.examples.tabcomponent.JTabComponentDemo" }
    };

    public ExampleList()
    {
        super("Examples");

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints iC = new GridBagConstraints();
        iC.gridx = 0;
        iC.gridy = GridBagConstraints.RELATIVE;
        iC.anchor = GridBagConstraints.LINE_START;
        iC.weightx = 1;
        iC.weighty = 1;
        iC.insets = new Insets(10, 5, 10, 5);

        GridBagConstraints bC = new GridBagConstraints();
        bC.gridx = 1;
        bC.gridy = GridBagConstraints.RELATIVE;
        bC.anchor= GridBagConstraints.LINE_START;
        bC.insets = new Insets(0, 0, 0, 10);

        for (String[] r : examples)
        {
            final String name =  r[0];
            final String clazz = r[1];

            panel.add( new JLabel( "<html><h3>"+name+"</h3></html>" ), iC );
            JButton start = new JButton("Start");
            start.addActionListener((evt) ->
            {
                try
                {
                    setVisible(false);
                    dispose();
                    Method main = Class.forName(clazz).getMethod("main", String[].class );
                    main.invoke(null, new Object[] { new String[0] });
                }
                catch ( Exception e)
                {
                    e.printStackTrace();;
                    JExceptionDialog ed = new JExceptionDialog(this, "Example", "Failed to start '"+name+"'", e);
                    ed.setLocationByPlatform(true);
                    ed.setVisible(true);                }
            });
            panel.add( start, bC );
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(panel);

        setPreferredSize(new Dimension(400, 400));
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }


    public static void main(String[] args)
    {
        new ExampleList();
    }

}
