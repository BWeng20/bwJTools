/*
 * (c) copyright Bernd Wengenroth
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

import java.awt.*;

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
     * Escapes special characters to use in UI HTML-code.
     * @param s The text to escape.
     * @return The escaped HTML code.
     */
    public static String escapeHTML(final String s)
    {
       final int n = s.length();
       StringBuilder sb = new StringBuilder(n*2);
       return escapeHTML(s, 0, n, sb ).toString();
    }

    /**
     * Adds escaped characters to use in UI HTML-code.
     * @param s The text to escape.
     * @param start Start index (inclusive).
     * @param end End index (exclusive).
     * @param sb The StringBuilder to append to.
     * @return sb
     */
    public static StringBuilder escapeHTML(final String s, int start, final int end, final StringBuilder sb)
    {
       while (start < end)
       {
           final char c = s.charAt(start++);
           switch ( c )
           {
            case '<': sb.append("&lt;"); break;
            case '>': sb.append("&gt;"); break;
            case '&': sb.append("&amp;"); break;
            default:  sb.append(c); break;
           }
      }
      return sb;
    }


    /**
     * Calculates a color with hight contrast.<br>
     * Should be used to calculate e.g. a front-color based on a dynamic background-color.
     *
     * @param col Color to use as reference.
     * @return Block or White, depending on input.
     */
    public static Color calculateContrastColor( Color col )
    {
        return calculateContrastColor( col, Color.WHITE, Color.BLACK);
    }

    /**
     * Calculates a color with hight contrast.<br>
     * Should be used to calculate e.g. a front-color based on a dynamic background-color.
     *
     * @param col Color to use as reference.
     * @param light Light Color to use if reference color is dark.
     * @param dark Dark Color to use if reference color is light.
     * @return Block or White, depending on input.
     */
    public static Color calculateContrastColor( Color col, Color light, Color dark )
    {
        return (calculateLumiance(col) < 130) ? light : dark;
    }

    public static float calculateLumiance( Color col )
    {
        // Calculate lumiance. See https://en.wikipedia.org/wiki/Relative_luminance
        return 0.2126f*col.getRed()+ 0.7152f*col.getGreen() + 0.0722f*col.getBlue();
    }

    /**
     * PLace a window at side of some other component.<br>
     * If it fit the location is chosen in this order:
     * <ol>
     *     <li>right</li>
     *     <li>left</li>
     *     <li>top</li>
     *     <li>bottom</li>
     * </ol>
     * @param component
     * @param toPlace
     */
    public static void placeAtSide( Component component, Window toPlace)
    {
        // Place the dialog at the side of the drop cap
        Point dcLocation = component.getLocationOnScreen();
        Rectangle dcRect = component.getBounds();

        Rectangle screenBounds = component.getGraphicsConfiguration().getBounds();
        Dimension windowSize = toPlace.getSize();

        Point target = new Point();

        int freeLeft   = (dcLocation.x - screenBounds.x);
        int freeRight  = (screenBounds.x+screenBounds.width) - (dcLocation.x + dcRect.width);
        int freeTop    = (dcLocation.y - screenBounds.y);
        int freeBottom = (screenBounds.y+screenBounds.height) - (dcLocation.y + dcRect.height);

        if ( freeRight >= windowSize.width)
        {
            target.x = dcLocation.x + dcRect.width;
            target.y = dcLocation.y;
        }
        else if ( freeLeft >= windowSize.width)
        {
            target.x = dcLocation.x - windowSize.width;
            target.y = dcLocation.y;
        }
        else if ( freeTop >= windowSize.height)
        {
            target.x = dcLocation.x;
            target.y = dcLocation.y-windowSize.height;
        }
        else if ( freeBottom >= windowSize.height)
        {
            target.x = dcLocation.x;
            target.y = dcLocation.y+dcRect.height;
        }
        else
        {
            target.x = dcLocation.x + dcRect.width;
            target.y = dcLocation.y;
        }

        if ( (target.y + windowSize.height) > (screenBounds.y+screenBounds.height) )
            target.y =  screenBounds.y + +screenBounds.height - windowSize.height;

        if ( (target.x + windowSize.width) > (screenBounds.x+screenBounds.width) )
            target.x =  screenBounds.x + +screenBounds.width - windowSize.width;

        toPlace.setLocation(target);
    }


}