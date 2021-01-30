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
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.icon.JColorIcon;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.JTabComponent;
import com.bw.jtools.ui.SettingsUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Demonstrates usage of JTabComponent.
 */
public class JTabComponentDemo
{
    JFrame frame;

    JTabbedPane tpane = new JTabbedPane(JTabbedPane.TOP);

    final Color colors[] = new Color[] {
        Color.BLUE  , Color.GREEN  , Color.GRAY      , Color.RED , Color.YELLOW,
        Color.ORANGE, Color.MAGENTA, Color.LIGHT_GRAY, Color.CYAN, Color.PINK
    };

    final Boolean used[] = new Boolean[]
    {
        Boolean.FALSE,Boolean.FALSE,Boolean.FALSE,Boolean.FALSE,Boolean.FALSE,
        Boolean.FALSE,Boolean.FALSE,Boolean.FALSE,Boolean.FALSE,Boolean.FALSE
    };

    void insertTab( int atIndex )
    {
        final JPanel tabpanel = new JPanel( new BorderLayout() );
        tabpanel.setPreferredSize(new Dimension(500,300));

        int idx=0;
        for ( ; idx<used.length; ++idx)
        {
            if (!used[idx]) break;
        }
        if ( idx >= used.length) return;

        used[idx] = true;

        String name = I18N.getText( ""+(idx+1));

        final int index = idx;
        used[index] = true;
        final JLabel l = new JLabel( I18N.format("IAm",name) );
        l.setHorizontalAlignment(SwingConstants.CENTER);
        tabpanel.add( l, BorderLayout.CENTER );

        l.addHierarchyListener(new HierarchyListener()
        {
            @Override
            public void hierarchyChanged(HierarchyEvent arg0)
            {
                used[index] = frame.isAncestorOf(l);
            }
        });

        // Text and icon in this call are replaces by the following
        // setTabComponentAt call.
        tpane.insertTab( "", null, tabpanel, null, atIndex );

        JColorIcon icon = new JColorIcon(14,14, colors[ idx ]);
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

        tpane.setSelectedIndex(atIndex);
        tabPane_lastIndex = atIndex;

    }

    static public void main( String args[] )
    {
        new JTabComponentDemo();
    }

    int tabPane_lastIndex = 0;

    public JTabComponentDemo()
    {
        // Initialize library.
        Application.initialize( JTabComponentDemo.class );
        I18N.addBundle("com.bw.jtools.examples.tabcomponent.i18n", JTabComponentDemo.class);

        // The library is now initialized from the "defaultsettings.properties"
        // parallel to the main-class.

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        frame = new JFrame( I18N.getText("Title"));

        JPanel mainPanel = new JPanel( new BorderLayout());
        mainPanel.add(tpane, BorderLayout.CENTER );
        frame.setContentPane(mainPanel);

        insertTab( 0 );
        insertTab( 1 );
        insertTab( 2 );

        final JLabel plusTab = new JLabel("");

        // Now we add a "+"-Button at the end of the tab that
        // shall add a new tab if the user selects it.

        int plusIndex = tpane.getTabCount();
        tpane.addTab("", plusTab );
        tpane.setTabComponentAt(plusIndex, new JTabComponent(
                "",null, () ->
                {
                    insertTab( 0 );
                },
                IconTool.getIcon("plus.png"), IconTool.getIcon("plus_ro.png")));

        // we have to ensure that this empty tab is never really
        // activated, so we add a change listener.
        // E.g. if user clicks on th etab, outside the action button.
        tpane.addChangeListener((changeEvent) ->
        {
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
               }
           }
           else
           {
              tabPane_lastIndex = tpane.getSelectedIndex();
           }
        });
        JLAFComboBox lafCB = new JLAFComboBox();
        mainPanel.add(lafCB, BorderLayout.SOUTH );

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages( IconTool.getAppIconImages() );
        frame.pack();

        // Restore window-position and dimension from prefences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose( frame );
        frame.setVisible(true);

        Log.info("Started");

    }
}

