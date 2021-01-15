package com.bw.jtools.examples.properties;

import com.bw.jtools.Application;
import com.bw.jtools.properties.*;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Property Demo.<br>
 */
public class PropertyDemoApplication
{
    protected static List<PropertyGroup> groups_ = new ArrayList<>();

    public static void main(String[] args)
    {
        // Initialize library.
        Application.initialize( PropertyDemoApplication.class );

        // The library has now initialized itself from the defaultsettings.properties.
        // parallel to the main-class.

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

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
        pg1.addProperty(new PropertyBooleanValue("PropertyBooleanNode(null)", null ) );
        pg1.addProperty(new PropertyColorValue("PropertyColorNode(new Color(200,200,200 ))", new Color(200,200,200 )) );
        pg1.addProperty(new PropertyStringValue("PropertyStringNode(null)", null ) );

        groups_.add(pg1);

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
        pg2.addProperty( "addProperty(Boolean.class) no null", Boolean.class ).nullable_ = false;

        groups_.add(pg2);

        PropertySheetWindow sheet = new PropertySheetWindow( groups_ );
        PropertyTableWindow table = new PropertyTableWindow( groups_ );
    }


}
