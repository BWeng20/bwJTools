package com.bw.jtools.examples.properties;

import com.bw.jtools.Application;
import com.bw.jtools.properties.PropertyGroup;

import javax.swing.UIManager;
import java.util.List;

/**
 * Demo for all property-editor alternatives.<br>
 */
public class PropertyDemoApplication
{
    public static final String[][] DESCRIPTION =
    {
            { "en", "Demonstrates parallel usage of multiple property<br>sheets/tables/tiles." },
            { "de", "Demonstriert die Verwendung von mehreren<br>parallelen Property-Fenstern." }
    };

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

        List<PropertyGroup> groups = PropertySheetWindow.createGroups();

        PropertySheetWindow sheet = new PropertySheetWindow(groups);
        PropertyTableWindow table = new PropertyTableWindow(groups);
        PropertyTileWindow tiles = new PropertyTileWindow(groups);

    }




}
