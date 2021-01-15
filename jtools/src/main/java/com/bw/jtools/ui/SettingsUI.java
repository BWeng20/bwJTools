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
package com.bw.jtools.ui;

import com.bw.jtools.persistence.Store;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Collection of methods to store or load settings for Swing elements.<br>
 * For swinf special methods see {@link SettingsSwing SettingsSwing}.
 */
public class SettingsUI
{
    /**
     * The default settings prefix for window-position and -size.
     */
    public static final String DEFAULT_WINDOW_PREFIX = "Window";

    /**
     * Get a dimension from preferences.<br>
     * Returns (0,0) if settings are missing.
     * @param pref_prefix The preference-key to use.
     * @return The dimension. (0,0) if settings are missing.
     */
    public static Dimension getDimension( String pref_prefix)
    {
      Dimension dim = new Dimension(0,0);
      dim.width  = Store.getInt( pref_prefix+".width", 0 );
      dim.height = Store.getInt( pref_prefix+".height", 0 );
      return dim;
    }

    /**
     * Loads window position from preferences with default key.
     * @see #loadWindowPosition(java.awt.Window, java.lang.String)
     * @param w  The window to store position and dimension.
     */
    public static void loadWindowPosition(Window w)
    {
        loadWindowPosition( w, DEFAULT_WINDOW_PREFIX );
    }

    /**
     * Restore a window position and dimension from preferences.
     * If settings are missing, the window is automatically positioned by
     * calling "setLocationByPlatform(true)".
     * @param w  The window to load position and dimension for.
     * @param pref_prefix The preferences prefix without ".". E.g. "window".
     */
    public static void loadWindowPosition(Window w, String pref_prefix)
    {
      Dimension dim = new Dimension(0,0);
      dim.width  = Store.getInt( pref_prefix+".width", 0 );
      dim.height = Store.getInt( pref_prefix+".height", 0 );
      if ( dim.width > 100 && dim.height > 100 )
         w.setSize(dim);

      Point pos = new Point(0,0);
      pos.x = Store.getInt(pref_prefix+".x", -1 );
      pos.y = Store.getInt(pref_prefix+".y", -1 );
      if ( pos.x >= 0 && pos.y >= 0 )
         w.setLocation(pos);
      else
         w.setLocationByPlatform(true);
    }

    /**
     * Stores window position to preferences with default key.
     * @see #storeWindowPosition(java.awt.Window, java.lang.String)
     * @param w  The window to store position and dimension.
     */
    public static void storeWindowPosition(Window w)
    {
        storeWindowPosition( w, DEFAULT_WINDOW_PREFIX );
    }

    /**
     * Stores window position to preferences.
     * @param w  The window to store position and dimension.
     * @param pref_prefix The preferences prefix without ".". E.g. "window".
     */
    public static void storeWindowPosition(Window w, String pref_prefix)
    {
       Dimension dim = w.getSize();
       Point pos = w.getLocation();

       if ( dim.width  < 200 ) dim.width = 100;
       if ( dim.height < 100 ) dim.height = 100;

       Store.setInt( pref_prefix+".width" , dim.width );
       Store.setInt( pref_prefix+".height", dim.height );

       Store.setInt( pref_prefix+".x", pos.x );
       Store.setInt( pref_prefix+".y", pos.y );
    }

    protected static class AutoFlushPosWindowListerner extends WindowAdapter
    {
        public final String pref_prefix_;

        public AutoFlushPosWindowListerner(String pref_prefix)
        {
            pref_prefix_ = pref_prefix;
        }

        @Override
        public void windowClosing(WindowEvent e)
        {
            // Store window-positon and size.
            SettingsSwing.storeWindowPosition(e.getWindow(), pref_prefix_ );

            // Most important: Make any change persistent:
            Store.flushStorage();
        }
    };

    protected static WindowListener autoFlushWindowListerner_ = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            // Make any change persistent:
            Store.flushStorage();
        }
    };

    /**
     * Installs a window-listener on the window that stores the window-position
     * and dimension and flushes the storage on close.
     * @param w The window.
     */
    public static void storePositionAndFlushOnClose( Window w )
    {
        storePositionAndFlushOnClose( w, DEFAULT_WINDOW_PREFIX );
    }

    /**
     * Installs a window-listener on the window that stores the window-position
     * and dimension and flushes the storage on close.
     * @param w The window.
     * @param pref_prefix The preferences prefix without ".". E.g. "window".
     */
    public static void storePositionAndFlushOnClose( Window w, String pref_prefix )
    {
        w.addWindowListener(new AutoFlushPosWindowListerner(pref_prefix));
    }


    /**
     * Installs a window-listener on the window that flushes the storage on close.
     * @param w The window.
     */
    public static void flushOnClose( Window w )
    {
        w.removeWindowListener(autoFlushWindowListerner_);
        w.addWindowListener(autoFlushWindowListerner_);
    }


}
