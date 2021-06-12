/*
 * Copyright Bernd Wengenroth.
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

import com.bw.jtools.Application;
import com.bw.jtools.examples.graph.JGraphDemo;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JExceptionDialog;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.swing.*;

/**
 * Main for Example Jar with Demo-selection.
 */
public final class ExampleList extends JFrame
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -1470942606169822654L;
	
	static String[][] examples =
    {
        { "Application Icons", "com.bw.jtools.examples.applicationicons.ApplicationIconsDemo" }  ,
        { "Data Table", "com.bw.jtools.examples.datatable.DataDemo" }  ,
        { "Exception Dialog", "com.bw.jtools.examples.exceptiondialog.JExceptionDialogDemo" }  ,
        { "Property Table", "com.bw.jtools.examples.properties.PropertyTableWindow" }  ,
        { "Property Sheet", "com.bw.jtools.examples.properties.PropertySheetWindow" }  ,
        { "Property Tile", "com.bw.jtools.examples.properties.PropertyTileWindow" }  ,
        { "Property Multiple", "com.bw.jtools.examples.properties.PropertyDemoApplication" }  ,
        { "Tab Component", "com.bw.jtools.examples.tabcomponent.JTabComponentDemo" },
        { "Path Chooser", "com.bw.jtools.examples.pathchooser.JPathChooserDemo" },
        { "Swing UI-Default", "com.bw.jtools.examples.uidefaults.UIDefaults" }
    };

	private class ExampleWindowWatcher extends WindowAdapter
    {
        int count = 0;
        @Override
        public void windowClosed(WindowEvent e)
        {
            if (--count == 0)
            {
                setVisible(true);
            }
            e.getWindow().removeWindowListener(this);
        }

        ExampleWindowWatcher()
        {
            Window[] ws = Window.getOwnerlessWindows();
            if (ws != null)
            {
                Window myWindow = SwingUtilities.getWindowAncestor(ExampleList.this);
                for (Window w : ws)
                {
                    if (w != myWindow && w.isShowing())
                    {
                        ++count;
                        w.addWindowListener(this);
                    }
                }
            }
            if ( count == 0 )
                setVisible(true);
        }
    }

    public ExampleList()
    {
        super(I18N.getText( "Demo.Title" ));

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints iC = new GridBagConstraints();
        iC.gridx = 0;
        iC.gridy = GridBagConstraints.RELATIVE;
        iC.anchor = GridBagConstraints.LINE_START;
        iC.weightx = 1;
        iC.weighty = 1;
        iC.insets = new Insets(10, 5, 10, 5);
        iC.fill = GridBagConstraints.HORIZONTAL;

        for (String[] r : examples)
        {
            final String name =  r[0];
            final String clazz = r[1];
            final String lang = Locale.getDefault().getLanguage();

            try
            {
                Class c = Class.forName(clazz);

                String description = null;
                try
                {
                    // try to get additional descrption from some static field "DESCRIPTION"
                    String[][] desc = (String[][])c.getField("DESCRIPTION").get(null);

                    for ( String[] d : desc )
                        if ( new Locale(d[0]).getLanguage().equals(lang) ) {
                            description = d[1];
                            break;
                        }
                    if ( description == null && desc.length>0)
                        description = desc[0][1];

                }
                catch (Exception fc)
                {}
                if ( description == null )
                    description = "";

                JButton start = new JButton("<html><h3>"+name+"</h3><p style='color:#505050;'>"+description+"</p><p>&nbsp;</p></html>");
                start.setHorizontalAlignment( SwingConstants.LEFT );
                start.addActionListener((evt) ->
                {
                    try
                    {
                        setVisible(false);
                        Method main = Class.forName(clazz).getMethod("main", String[].class );
                        main.invoke(null, new Object[] { new String[0] });

                        new ExampleWindowWatcher();

                    }
                    catch ( Exception e)
                    {
                        e.printStackTrace();;
                        JExceptionDialog ed = new JExceptionDialog(this, "Example", "Failed to start '"+name+"'", e);
                        ed.setLocationByPlatform(true);
                        ed.setVisible(true);
                        setVisible(true);
                    }
                });
                panel.add( start, iC );
            }
            catch ( Exception e)
            {

            }
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(panel);

        setPreferredSize(new Dimension(500, 850));
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }


    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        I18N.addBundle("com.bw.jtools.examples.starter.i18n", ExampleList.class);
        new ExampleList();

    }

}
