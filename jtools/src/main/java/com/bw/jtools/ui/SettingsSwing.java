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

import javax.swing.*;

/**
 * Collection of methods to store or load settings for Swing elements.
 * @see SettingsUI
 */
public class SettingsSwing extends SettingsUI
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
}
