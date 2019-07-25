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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import com.bw.jtools.persistence.Store;
import javax.swing.JSplitPane;

/**
 * Collection of methods to store or load settings for Swing elements.
 *
 */
public class SettingsSwing
{
    /**
     * Restores a JSplitPane splitter position from preferences.<br>
     * Splitter position is restricted to 50 pixel. Values less will be ignored.
     * See {@link #storeSplitPosition(javax.swing.JSplitPane, java.lang.String) storeSplitPosition}.
     * @param splitter  The Splitter.
     * @param pref_prefix The preferences-prefix to use.
     */
    public static void loadSplitPosition(JSplitPane splitter, String pref_prefix )
    {
        int pos = Store.getInt( pref_prefix+".divider" , -1 );
        if ( pos > 50 )
        {
            splitter.setDividerLocation(pos);
        }
    }

    /**
     * Stores a JSplitPane splitter position to preferences.<br>
     * See {@link #loadSplitPosition(javax.swing.JSplitPane, java.lang.String) loadSplitPosition}.
     * @param splitter  The Splitter.
     * @param pref_prefix The preferences-prefix to use.
     */
    public static void storeSplitPosition(JSplitPane splitter, String pref_prefix )
    {
        int pos = splitter.getDividerLocation();
        Store.setInt( pref_prefix+".divider" , pos );
    }


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
}
