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
package com.bw.jtools.examples.propertytable;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.properties.PropertyBooleanValue;
import com.bw.jtools.ui.properties.PropertyColorValue;
import com.bw.jtools.ui.properties.PropertyEnumValue;
import com.bw.jtools.ui.properties.PropertyGroup;
import com.bw.jtools.ui.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyNumberValue;
import com.bw.jtools.ui.properties.PropertyStringValue;
import com.bw.jtools.ui.properties.PropertyTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultTreeModel;

/**
 * An example enum to be shown in a combo-box.
 */
enum MyEnum
{
    ONE,
    TWO,
    THREE,
    FOUR
}

/**
 * Demonstration for the property-editor.
 */
public class PropertyTableDemo
{
    static String LAF = null;
    static LookAndFeelInfo[] lafs;
    static JFrame frame;

    static public void main( String args[] )
    {
        // Initialize library.
        Application.initialize( PropertyTableDemo.class );

        // The librarie has now initialized itself from the defaultsettings.properties.
        // parallel to the main-class.

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        PropertyTable table = new PropertyTable();

        DefaultTreeModel model = table.getTreeModel();

        PropertyGroup root = new PropertyGroup("Root");

        PropertyGroup p = new PropertyGroup("Group I - Node Wrapper Classes.");
        root.add( p );


        p.add(new PropertyNumberValue("PropertyNumberNode(12234)", 1234 ) );

        PropertyValue nb2 = new PropertyValue("PropertyNode(Double.class) and own NumberFormat", Double.class );
        // Use more fraction digits by specifing a dedicated NumberFormat.
        nb2.nf_ = NumberFormat.getNumberInstance();
        nb2.nf_.setMaximumFractionDigits(10);
        nb2.setUserObject( 123.4567 );
        p.add( nb2 );

        PropertyValue nb3 = new PropertyValue("PropertyNode(Double.class) with default fraction", Double.class );

        // Because the values has more fraction-digits than the default decimal format,
        // the table will change the value to "123.456" if leaving the edit-mode, even if use has changed nothing!
        // You will see an update of the "tableChanged"-counter in status-line if this happens.
        nb3.setUserObject( 123.4567 );
        p.add( nb3 );

        // Normally you will not simply add properties. You will store a reference to the "ProperyValue" instance
        // (or of the used derived class) and use this instance to sync the data with your own data-modell.
        p.add(new PropertyEnumValue("PropertyEnumNode<MyEnum>(MyEnum.FOUR)", MyEnum.FOUR ) );
        p.add(new PropertyEnumValue("PropertyEnumNode<MyEnum>(MyEnum.class )", MyEnum.class ) );
        p.add(new PropertyBooleanValue("PropertyBooleanNode(null)", null ) );
        p.add(new PropertyColorValue("PropertyColorNode(new Color(200,200,200 ))", new Color(200,200,200 )) );
        p.add(new PropertyStringValue("PropertyStringNode(null)", null ) );

        PropertyGroup p3 = new PropertyGroup("Group II - PropertyGroupNode.addProperty");

        p3.addProperty( "addProperty(MyEnum.FOUR)", MyEnum.FOUR );
        p3.addProperty( "addProperty(Integer.class)", Integer.class );
        p3.addProperty( "addProperty(1234)", 1234 );
        p3.addProperty( "addProperty(1234.12)", 1234.12 );
        p3.addProperty( "addProperty(Boolean.class)", Boolean.class );
        p3.addProperty( "addProperty(Boolean.TRUE)", Boolean.TRUE);
        p3.addProperty( "addProperty(String.class)", String.class);
        p3.addProperty( "addProperty(\"ABC\")", "ABC");
        p3.addProperty( "addProperty(Color.class)", Color.class);
        p3.addProperty( "addProperty(Color.BLUE)", Color.BLUE);
        p3.addProperty( "addProperty(Boolean.class) no null", Boolean.class ).nullable_ = false;

        root.add( p3 );
        model.setRoot(root);

        final JLabel updateCounter = new JLabel("#tableChanged 0    ");

        table.expandAll();
        // table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        table.getModel().addTableModelListener(new TableModelListener()
        {
            long counter = 0;
            NumberFormat nf = NumberFormat.getInstance();

            @Override
            public void tableChanged(TableModelEvent arg0)
            {
                updateCounter.setText("#tableChanged "+nf.format(++counter) );
            }
        });


        frame = new JFrame("Property Table Demo");
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 500));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JComboBox<String> lafCB = new JComboBox<>();
        lafs = UIManager.getInstalledLookAndFeels();
        for ( LookAndFeelInfo laf : lafs )
            lafCB.addItem( laf.getName() );

        LAF = UIManager.getLookAndFeel().getName();
        lafCB.setSelectedItem( LAF );

        lafCB.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent ie)
            {
                String lafName = (String)lafCB.getSelectedItem();
                if (!lafName.equals(LAF))
                {
                    LAF = lafName;
                    for ( LookAndFeelInfo laf : lafs )
                        if ( laf.getName().equals(lafName) )
                        {
                            try
                            {
                                UIManager.setLookAndFeel(laf.getClassName());
                                LAF = laf.getName();
                            } catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            frame.repaint();
                            break;
                        }
                }
            }
        });

        JPanel ctrlPanel = new JPanel(new BorderLayout());
        ctrlPanel.add(lafCB, BorderLayout.WEST);

        ctrlPanel.add( updateCounter, BorderLayout.EAST);

        panel.add(ctrlPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // IconCache tries to load all icons with the name-pattern
        // <application>
        frame.setIconImages( IconCache.getAppIconImages() );

        frame.setContentPane(panel);
        frame.pack();

        // Restore window-position and dimension from prefences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose( frame );

        frame.setVisible(true);

        Log.info("Started");

    }
}
