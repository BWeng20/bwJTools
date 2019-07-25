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
 * Facade to Swing / FX from UI independent source.
 * Currently it make no sense to introduce some interface-pattern for this stuff.
 */
public final class UITool
{
    static public boolean javaFX = false;

    // Execute code inside the FX Thread
    public static void executeInUIThread( Runnable r )
    {
       UIToolSwing.executeInUIThread(r);
    }

    public static void showWait( boolean enable )
    {
        WaitSplash.showWait(enable);
    }

    public static void showWait( boolean enable, String message )
    {
        WaitSplash.showWait(enable, message);
    }


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

    public static Color calculateContrastColor( Color background )
    {
       // Calculate lumiance. See https://en.wikipedia.org/wiki/Relative_luminance
       final float luminance = 0.2126f*background.getRed()+ 0.7152f*background.getGreen() + 0.0722f*background.getBlue();
       return (luminance < 130) ? Color.WHITE : Color.BLACK;
    }


    private static Locale currentLocale_;
    private static ResourceBundle resourceBundle_;

   // Send all output to the Appendable object sb
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
     * Gets a static text.
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
     * Applies a format-string.
     * @param key Text-Id that is used to get the text from the resource-bundle.
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
