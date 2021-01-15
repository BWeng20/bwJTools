package com.bw.jtools.examples.properties;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.properties.*;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
        frame_.setIconImages( IconCache.getAppIconImages() );

        statusPanel_ = new JPanel(new BorderLayout());
        JLAFComboBox lafCB = new JLAFComboBox();
        statusPanel_.add(lafCB, BorderLayout.WEST);
        status_ = new JTextField();
        status_.setEditable(false);
        statusPanel_.add(status_, BorderLayout.CENTER);

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

    protected abstract void propertyChanged( PropertyValue value );
}
