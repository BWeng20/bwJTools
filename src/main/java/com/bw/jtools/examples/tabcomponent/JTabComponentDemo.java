/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.examples.tabcomponent;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.JColorIcon;
import com.bw.jtools.ui.JTabComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * Demonstrates usage of JTabComponent.
 */
public class JTabComponentDemo
{
    static String LAF = null;
    static UIManager.LookAndFeelInfo[] lafs;
    static JFrame frame;

    static JTabbedPane tpane = new JTabbedPane(JTabbedPane.TOP);

    static Color colors[] = new Color[] {
        Color.BLUE, Color.GREEN, Color.GRAY,
        Color.RED, Color.YELLOW, Color.ORANGE,
        Color.MAGENTA, Color.LIGHT_GRAY, Color.CYAN
    };

    static void insertTab( int atIndex, String name )
    {
        final JPanel tabpanel = new JPanel( new BorderLayout() );
        tabpanel.setPreferredSize(new Dimension(300,300));
        tabpanel.add( new JLabel(name), BorderLayout.CENTER );

        // Text and icon in this call are replaces by the following
        // setTabComponentAt call.
        tpane.insertTab( "", null, tabpanel, "I am "+name, atIndex );

        JColorIcon icon = new JColorIcon(14,14,
                colors[ tpane.getTabCount() % colors.length]);
        icon.setBorderPainted(false);

        tpane.setTabComponentAt(atIndex, new JTabComponent(name,
                icon,() ->
            {
                int tabIdx = tpane.indexOfComponent(tabpanel);
                if (tabIdx != -1)
                {
                    tpane.remove(tabIdx);
                }
            }));

    }

    static public void main( String args[] )
    {

        // Initialize library.
        Application.initialize( JTabComponentDemo.class );

        // The library is now initialized from the "defaultsettings.properties"
        // parallel to the main-class.

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        frame = new JFrame("JTabComponent Demonstration");

        insertTab( 0, "Tab One"  );
        insertTab( 1, "Tab Two"  );
        insertTab( 2, "Tab Three");

        final JLabel plusTab = new JLabel("");

        // Now we add a "+"-Button at the end of the tab that
        // shall add a new tab if the user selects it.

        int plusIndex = tpane.getTabCount();
        tpane.addTab("", plusTab );
        tpane.setTabComponentAt(plusIndex, new JTabComponent("",null,null,
                IconCache.getIcon("plus.png"), IconCache.getIcon("plus_ro.png")));

        // we have to ensure that this empty tab is never really
        // activates, so we add a change listener.
        // The listener can add new tab or switch back ti the last
        // tab. E.g. you may need to open a document for the tab.
        // If the user cancel this operation or if it fails,
        // you have to switch back to the previous tab, otherwise
        // the empty "+"-Tab will get active.
        tpane.addChangeListener((changeEvent) ->
        {
           int tabPane_lastIndex = 0;

           final Component selectedComp = tpane.getSelectedComponent();
           if ( selectedComp == plusTab )
           {
               if ( tpane.getTabCount() == 1)
               {
                   // User closed all tabs beside "+"
                   frame.setVisible(false);
                   frame.dispose();
               }
               else
               {
                    tpane.setSelectedIndex(tabPane_lastIndex);
                    if ( tpane.getTabCount() < 10 )
                    {
                        insertTab( 0, "New Tab "+tpane.getTabCount() );
                        tpane.setSelectedIndex(0);
                    }
               }
           }
           else
           {
              tabPane_lastIndex = tpane.getSelectedIndex();
           }
        });


        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages( IconCache.getAppIconImages() );
        frame.setContentPane(tpane);
        frame.pack();

        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        Log.info("Started");

    }
}

