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

import com.bw.jtools.Log;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Simple wrapper to ease i18n stuff.<br>
 */
public final class I18N
{
    private static Locale currentLocale_;
    private static ArrayList<BundleInfo> resourceBundles_ = new ArrayList<>();
    private final static String empty_string = "";

    static class BundleInfo
    {
        Class<?>       clazz_;
        String         name_;
        ResourceBundle bundle_;
    }


    static
    {
        setLocale(null);
        addBundle( "com.bw.jtools.ui.i18n", I18N.class );
    }

    /**
     * Sets the locale to use for i18n. Use null to restore system default.<br>
     * Reloads all bundles if locale has effectively changed.
     * @param locale The local to use or null.
     */
    public static void setLocale( Locale locale )
    {
        Locale newLocale = (null == locale) ? Locale.getDefault() : locale ;
        if ( currentLocale_ == null || !currentLocale_.equals(newLocale) )
        {
            currentLocale_ = newLocale;
            for( BundleInfo bi : resourceBundles_)
            {
                bi.bundle_ = ResourceBundle.getBundle(bi.name_, currentLocale_, bi.clazz_.getClassLoader() );
            }
        }
    }

    /**
     * Adds an application specific bundle.<br>
     * Bundles will be used in opposite order as added.
     * @param name The resource name in package-syntax. E.g. "com.bw.jtools.ui.i18n"
     * @param clazz The class to use for loading.
     */
    public static void addBundle( String name, Class<?> clazz )
    {
        BundleInfo bi = new BundleInfo();
        bi.name_  = name;
        bi.clazz_ = clazz;
        bi.bundle_ = null;
        try{
            bi.bundle_ = ResourceBundle.getBundle(name, currentLocale_, clazz.getClassLoader() );
        }
        catch (Exception e)
        {
        }
        if ( bi.bundle_ == null )
        {
            Log.warn("Failed to load bundle '"+name+"' by class-loader for "+clazz.getSimpleName() );
        }
        else
        {
            resourceBundles_.add(0, bi);
        }
    }


    /**
     * Gets a static text from resource bundles.
     * @param key Text-Id that is used to get the text from the resource-bundle.
     * @return The language dependent text.
     */
    public static String getText( String key )
    {
        for ( BundleInfo bi : resourceBundles_ )
        {
            try
            {
                final String text = bi.bundle_.getString(key);
                if ( text != null && !text.isEmpty() )
                    return text;
            }
            catch (Exception e)
            {
            }
        }
        Log.warn("Missing i18n text '"+key+"'");
        return key;
    }

    /**
     * Check is a key is available in the resource bundles.
     * @param key Text-Id.
     * @return true if the key is available.
     */
    public static boolean hasText( String key )
    {
        for ( BundleInfo bi : resourceBundles_ )
        {
            try
            {
                final String text = bi.bundle_.getString(key);
                if ( text != null && !text.isEmpty() )
                    return true;
            }
            catch (Exception e)
            {
            }
        }
        return false;
    }


    /**
     * Applies a i18n text format-string.
     * @see #getText(java.lang.String)
     *
     * @param key Text-Id that is used to get the text from the resource-bundles.
     * @param arguments Argument list used in the format-pattern.
     * @return The formatted language dependent text.
     */
    public static String format( String key, Object... arguments)
    {
        String f = getText(key);
        Formatter formatter  = null;
        try 
        {
	        if ( f != null )
	        {
	            formatter = new Formatter(currentLocale_);
	            formatter.format(f, arguments);
	            String r = formatter.out().toString();
	            return r;
	        }
        } finally {
			if (formatter != null) {
				formatter.close();
			}
		}
        return empty_string;
    }

}
