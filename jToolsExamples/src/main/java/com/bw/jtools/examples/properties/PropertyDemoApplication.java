package com.bw.jtools.examples.properties;

import com.bw.jtools.Application;
import com.bw.jtools.properties.*;

import javax.swing.*;
import java.util.List;

/**
 * Demo for all property-editor alternatives.<br>
 */
public class PropertyDemoApplication
{

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
