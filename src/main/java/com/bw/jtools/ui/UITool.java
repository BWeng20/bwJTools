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

import java.awt.Color;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Facade for creating Swing independent source-code and other
 * UI related stuff.<br>
 */
public final class UITool
{
    /**
     * Execute code inside the UI Thread.<br>
     * Enables us to switch to other UI system, e.g.JavFX / Swing /AWT. Currently only Swing.<br>
     * FX code was removed because FX is not really good for writing complex interactive widgets...
     * @param r The runnable to execute.
     */
    public static void executeInUIThread( Runnable r )
    {
        UIToolSwing.executeInUIThread(r);
    }

    /**
     * Shows "please wait".
     * @param enable If true the wait-screen is shown, if false it is hidden.
     * @see WaitSplash#showWait(boolean)
     */
    public static void showWait( boolean enable )
    {
        WaitSplash.showWait(enable);
    }

    /**
     * Shows "please wait".
     * @param enable If true the wait-screen is shown, if false it is hidden.
     * @param message The message to show.
     * @see WaitSplash#showWait(boolean, java.lang.String)
     */
    public static void showWait( boolean enable, String message )
    {
        WaitSplash.showWait(enable, message);
    }

    /**
     * Escapes special characters to use in html-code.
     * @param s The test to escape.
     * @return The escaped html code.
     */
    public static String escapeHTML(String s)
    {
       final int n = s.length();
       StringBuilder sb = new StringBuilder(n*2);
       for (int i = 0; i < n; ++i)
       {
           final char c = s.charAt(i);
           switch ( c )
           {
            case '<': sb.append("&lt;"); break;
            case '>': sb.append("&gt;"); break;
            case '&': sb.append("&amp;"); break;
            default:  sb.append(c); break;
           }
      }
      return sb.toString();
    }

    /**
     * Calculates a color with hight contrast.<br>
     * Should be used to caluclate e.g. a front-color based on a dynamic background-color.
     *
     * @param col Color to use as reference.
     * @return Block or White, depending on input.
     */
    public static Color calculateContrastColor( Color col )
    {
       // Calculate lumiance. See https://en.wikipedia.org/wiki/Relative_luminance
       final float luminance = 0.2126f*col.getRed()+ 0.7152f*col.getGreen() + 0.0722f*col.getBlue();
       return (luminance < 130) ? Color.WHITE : Color.BLACK;
    }

    private static Locale currentLocale_;
    private static ResourceBundle resourceBundle_;

    private final static String empty_string = "";


    static
    {
        setI18Locale(null);
    }

    /**
     * Sets the locate to use for i18n.
     * Use null to restore system default.
     * @param locale The local to use or null.
     */
    public static void setI18Locale( Locale locale )
    {
        currentLocale_ = (null == locale) ? Locale.getDefault() : locale ;
        resourceBundle_ = null;
    }

    /**
     * Gets a static text from library resource bundle.
     * @param key Text-Id that is used to get the text from the resource-bundle.
     * @return The language dependent text.
     */
    public static String getI18NText( String key )
    {
        ResourceBundle bundle = resourceBundle_;
        if ( bundle == null )
        {
            bundle = ResourceBundle.getBundle("com.bw.jtools.ui.i18n", currentLocale_, UITool.class.getClassLoader() );
            resourceBundle_ = bundle;
        }
        return bundle.getString(key);
    }

    /**
     * Applies a i18n text format-string.
     * @see #getI18NText(java.lang.String)
     *
     * @param key Text-Id that is used to get the text from the library-resource-bundle.
     * @param arguments Argument list used in the format-pattern.
     * @return The formatted language dependent text.
     */
    public static String formatI18N( String key, Object... arguments)
    {
        String f = getI18NText(key);
        if ( f != null )
        {
            Formatter formatter = new Formatter(currentLocale_);
            formatter.format(f, arguments);
            return formatter.out().toString();
        }
        return empty_string;
    }

}
