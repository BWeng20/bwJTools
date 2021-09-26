package com.bw.jtools.examples.properties;

import com.bw.jtools.Log;
import com.bw.jtools.properties.*;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Base class for table- and sheet-property windows in this demo.<br>
 * Common initialization.
 */
public abstract class PropertyDemoWindowBase
{
    protected JFrame frame_;
    protected Container contentPane_;
    protected JPanel statusPanel_;
    protected JTextField status_;
    protected JTextField lang_;

    protected final List<PropertyGroup> groups_ ;

    protected PropertyDemoWindowBase(String title, List<PropertyGroup> groups )
    {
        groups_ = groups;
        PropertyChangeListener l = value -> propertyChanged(value);

        for ( PropertyGroup pg : groups )
            pg.addPropertyChangeListener(l);

        frame_ = new JFrame(title);
        frame_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // IconCache tries to load all icons with the name-pattern
        // <application>
        frame_.setIconImages( IconTool.getAppIconImages() );

        statusPanel_ = new JPanel(new BorderLayout());
        JLAFComboBox lafCB = new JLAFComboBox();
        statusPanel_.add(lafCB, BorderLayout.WEST);
        status_ = new JTextField();
        status_.setEditable(false);
        statusPanel_.add(status_, BorderLayout.CENTER);

        lang_= new JTextField();
        lang_.setEditable(false);
        lang_.setText(Locale.getDefault().toString());
        statusPanel_.add(lang_, BorderLayout.EAST);

        contentPane_ = frame_.getContentPane();
        contentPane_.add(statusPanel_, BorderLayout.SOUTH);
        frame_.setContentPane(contentPane_);
    }

    protected void show( JComponent comp )
    {
        contentPane_.add(comp, BorderLayout.CENTER);

        frame_.pack();

        String key = getClass().getSimpleName();

        // Restore window-position and dimension from prefences.
        SettingsUI.loadWindowPosition(frame_, key );
        SettingsUI.storePositionAndFlushOnClose(frame_, key);

        frame_.setVisible(true);

        Log.info(key +" started");
    }

    public static List<PropertyGroup> createGroups()
    {
        List<PropertyGroup> groups = new ArrayList<>();

        PropertyGroup pg1 = new PropertyGroup("Group I - Node Wrapper Classes.");

        pg1.addProperty(new PropertyNumberValue("PropertyNumberNode(12234)", 12234 ) );

        PropertyValue nb2 = new PropertyValue("PropertyNode(Double.class) and own NumberFormat", Double.class );
        // Use more fraction digits by specifying a dedicated NumberFormat.
        nb2.nf_ = NumberFormat.getNumberInstance();
        nb2.nf_.setMaximumFractionDigits(10);
        nb2.setPayload( 123.4567 );
        pg1.addProperty( nb2 );

        PropertyValue nb3 = new PropertyValue("PropertyNode(Double.class) with default fraction", Double.class );

        // Because the values have more fraction-digits than the default decimal format,
        // the table will change the value to "123.456" if leaving the edit-mode,
        // even if user changed nothing! This could be avoided by specifying a number-format -
        // as shown above.
        nb3.setPayload( 123.4567 );
        pg1.addProperty( nb3 );

        // Normally you will not simply add properties. You will store a reference to the "ProperyValue" instance
        // (or of the used derived class) and use this instance to sync the data with your own data-model.
        pg1.addProperty(new PropertyEnumValue<MyEnum>("PropertyEnumNode<MyEnum>(MyEnum.FOUR)", MyEnum.FOUR ) );
        pg1.addProperty(new PropertyEnumValue<MyEnum>("PropertyEnumNode<MyEnum>(MyEnum.class )", MyEnum.class ) );

        PropertyValue choiceVal = new PropertyNumberValue("PropertyNumberValue(1).possibleValues_",2);
        choiceVal.possibleValues_ = new LinkedHashMap<>();
        choiceVal.possibleValues_.put("One", 1);
        choiceVal.possibleValues_.put("Two", 2);
        choiceVal.possibleValues_.put("Three", 3);
        pg1.addProperty(choiceVal);

        pg1.addProperty(new PropertyBooleanValue("PropertyBooleanNode(null)", null ) );
        pg1.addProperty(new PropertyColorValue("PropertyColorNode(new Color(200,200,200 ))", new Color(200,200,200 )) );
        pg1.addProperty(new PropertyStringValue("PropertyStringNode(null)", null ) );

        groups.add(pg1);

        PropertyGroup pg2 = new PropertyGroup("Group II - PropertyGroupNode.addProperty");
        pg2.addProperty( "addProperty(MyEnum.FOUR)", MyEnum.FOUR );
        pg2.addProperty( "addProperty(Integer.class)", Integer.class );
        pg2.addProperty( "addProperty(1234)", 1234 );
        pg2.addProperty( "addProperty(1234.12)", 1234.12 );
        pg2.addProperty( "addProperty(Boolean.class)", Boolean.class );
        pg2.addProperty( "addProperty(Boolean.TRUE)", Boolean.TRUE);
        pg2.addProperty( "addProperty(String.class)", String.class);
        pg2.addProperty( "addProperty(\"ABC\")", "ABC");
        pg2.addProperty( "addProperty(Color.class)", Color.class);
        pg2.addProperty( "addProperty(Color.BLUE)", Color.BLUE);
        pg2.addProperty( "addProperty(Font.class)", Font.class);
        pg2.addProperty( "addProperty(Boolean.class) no null", Boolean.class ).nullable_ = false;

        groups.add(pg2);

        return groups;
    }

    private int counter_ = 0;

    protected void propertyChanged( PropertyValue v )
    {
        ++counter_;
        SwingUtilities.invokeLater(() ->  status_.setText( ""+counter_+" propertyChanged '"+v.displayName_+"' \u21C9 "+v.getPayload()) );
    }


}
