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

import com.bw.jtools.image.ImageTool;

import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

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
	 *
	 * @param r The runnable to execute.
	 */
	public static void executeInUIThread(Runnable r)
	{
		UIToolSwing.executeInUIThread(r);
	}

	/**
	 * Shows "please wait".
	 *
	 * @param enable If true the wait-screen is shown, if false it is hidden.
	 * @see WaitSplash#showWait(boolean)
	 */
	public static void showWait(boolean enable)
	{
		WaitSplash.showWait(enable);
	}

	/**
	 * Shows "please wait".
	 *
	 * @param enable  If true the wait-screen is shown, if false it is hidden.
	 * @param message The message to show.
	 * @see WaitSplash#showWait(boolean, java.lang.String)
	 */
	public static void showWait(boolean enable, String message)
	{
		WaitSplash.showWait(enable, message);
	}

	/**
	 * Escapes special characters to use in UI HTML-code.
	 *
	 * @param s The text to escape.
	 * @return The escaped HTML code.
	 */
	public static String escapeHTML(final String s)
	{
		final int n = s.length();
		StringBuilder sb = new StringBuilder(n * 2);
		return escapeHTML(s, 0, n, sb).toString();
	}

	/**
	 * Adds escaped characters to use in UI HTML-code.
	 *
	 * @param s     The text to escape.
	 * @param start Start index (inclusive).
	 * @param end   End index (exclusive).
	 * @param sb    The StringBuilder to append to.
	 * @return sb
	 */
	public static StringBuilder escapeHTML(final String s, int start, final int end, final StringBuilder sb)
	{
		while (start < end)
		{
			final char c = s.charAt(start++);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				default:
					sb.append(c);
					break;
			}
		}
		return sb;
	}


	/**
	 * Calculates a color with hight contrast.<br>
	 * Should be used to calculate e.g. a front-color based on a dynamic background-color.
	 *
	 * @param paint Color to use as reference.
	 * @return Block or White, depending on input.
	 */
	public static Color calculateContrastColor(Paint paint)
	{
		if (paint instanceof Color)
			return calculateContrastColor((Color) paint, Color.WHITE, Color.BLACK);
		else if (paint instanceof GradientPaint)
		{
			GradientPaint g = (GradientPaint) paint;
			return calculateContrastColor(blendColors(g.getColor1(), g.getColor2(), 0.5f),
					Color.WHITE, Color.BLACK);
		}
		return Color.BLACK;
	}

	/**
	 * Calculates a color with hight contrast.<br>
	 * Should be used to calculate e.g. a front-color based on a dynamic background-color.
	 *
	 * @param col   Color to use as reference.
	 * @param light Light Color to use if reference color is dark.
	 * @param dark  Dark Color to use if reference color is light.
	 * @return Block or White, depending on input.
	 */
	public static Color calculateContrastColor(Color col, Color light, Color dark)
	{
		return (calculateLumiance(col) < 130) ? light : dark;
	}

	public static float calculateLumiance(Color col)
	{
		// Calculate lumiance. See https://en.wikipedia.org/wiki/Relative_luminance
		return 0.2126f * col.getRed() + 0.7152f * col.getGreen() + 0.0722f * col.getBlue();
	}

	/**
	 * Place a window at side of some other component.<br>
	 * If it fit the location is chosen in this order:
	 * <ol>
	 *     <li>right</li>
	 *     <li>left</li>
	 *     <li>top</li>
	 *     <li>bottom</li>
	 * </ol>
	 *
	 * @param component
	 * @param toPlace
	 */
	public static void placeAtSide(Component component, Window toPlace)
	{
		Point dcLocation = component.getLocationOnScreen();
		Rectangle dcRect = component.getBounds();

		Rectangle screenBounds = component.getGraphicsConfiguration()
										  .getBounds();
		Dimension windowSize = toPlace.getSize();

		Point target = new Point();

		int freeLeft = (dcLocation.x - screenBounds.x);
		int freeRight = (screenBounds.x + screenBounds.width) - (dcLocation.x + dcRect.width);
		int freeTop = (dcLocation.y - screenBounds.y);
		int freeBottom = (screenBounds.y + screenBounds.height) - (dcLocation.y + dcRect.height);

		if (freeRight >= windowSize.width)
		{
			target.x = dcLocation.x + dcRect.width;
			target.y = dcLocation.y;
		}
		else if (freeLeft >= windowSize.width)
		{
			target.x = dcLocation.x - windowSize.width;
			target.y = dcLocation.y;
		}
		else if (freeTop >= windowSize.height)
		{
			target.x = dcLocation.x;
			target.y = dcLocation.y - windowSize.height;
		}
		else if (freeBottom >= windowSize.height)
		{
			target.x = dcLocation.x;
			target.y = dcLocation.y + dcRect.height;
		}
		else
		{
			target.x = dcLocation.x + dcRect.width;
			target.y = dcLocation.y;
		}

		if ((target.y + windowSize.height) > (screenBounds.y + screenBounds.height))
			target.y = screenBounds.y + +screenBounds.height - windowSize.height;

		if ((target.x + windowSize.width) > (screenBounds.x + screenBounds.width))
			target.x = screenBounds.x + +screenBounds.width - windowSize.width;

		toPlace.setLocation(target);
	}

	/**
	 * Place a window on top of some other component.<br>
	 *
	 * @param component
	 * @param toPlace
	 */
	public static void placeOnTop(Component component, Window toPlace)
	{
		Point dcLocation = component.getLocationOnScreen();
		Rectangle dcRect = component.getBounds();

		int w = toPlace.getWidth();
		int h = toPlace.getHeight();


		Rectangle screenBounds = component.getGraphicsConfiguration()
										  .getBounds();

		Point target = new Point();

		target.x = dcLocation.x + dcRect.width / 2 - w / 2;
		target.y = dcLocation.y + dcRect.height / 2 - h / 2;

		if (target.x < screenBounds.x)
		{
			target.x = screenBounds.x;
		}
		else if ((target.x + w) > (screenBounds.x + screenBounds.width))
		{
			target.x = (screenBounds.x + screenBounds.width) - w;
		}
		if (target.y < screenBounds.y)
		{
			target.y = screenBounds.y;
		}
		else if ((target.y + h) > (screenBounds.y + screenBounds.height))
		{
			target.y = (screenBounds.y + screenBounds.height) - h;
		}
		toPlace.setLocation(target);
	}

	/**
	 * Multiply a color with some ratio.<br>
	 *
	 * @param col    The color
	 * @param factor The factor.
	 * @return
	 */
	public static Color multiplyColor(Color col, float factor) {

		final int r = (int) (factor * col.getRed());
		final int g = (int) (factor * col.getGreen());
		final int b = (int) (factor * col.getBlue());

		return new Color(r, g, b, col.getAlpha());
	}

	/**
	 * Blend two colors with some ratio.<br>
	 * First color proportion is "ration", second color
	 * proportion is "1-ration".
	 *
	 * @param col1  First color
	 * @param col2  Second Color
	 * @param ratio The ratio.
	 * @return
	 */
	public static Color blendColors(Color col1, Color col2, float ratio)
	{

		if (ratio < 0f)
		{
			ratio = 0;
		}
		else if (ratio > 1.0f)
		{
			ratio = 1.0f;
		}

		float secRatio = 1.0f - ratio;

		final int r = (int) ((ratio * col1.getRed()) + (secRatio * col2.getRed()));
		final int g = (int) ((ratio * col1.getGreen()) + (secRatio * col2.getGreen()));
		final int b = (int) ((ratio * col1.getBlue()) + (secRatio * col2.getBlue()));

		return new Color(r, g, b);
	}

	public static String formatStorageSizeBinary(NumberFormat nf, long size)
	{
		return formatStorageSize(nf, size, 1024, " B", " KiB", " MiB", " GiB", " TiB", " PiB", " EiB");
	}

	public static String formatStorageSizeDecimal(NumberFormat nf, long size)
	{
		return formatStorageSize(nf, size, 1000, " B", " kB", " MB", " GB", " TB", " PB", " EB");
	}

	public static String formatStorageSize(NumberFormat nf, long size, int base, CharSequence... postfixe)
	{
		if (nf == null)
		{
			nf = NumberFormat.getInstance();
		}
		// Remove sign
		long sizeUnsigned;
		StringBuilder sb = new StringBuilder();
		if (size < 0)
		{
			sb.append('-');
			// Avoid overflow for MIN_VALUE
			if (Long.MIN_VALUE == size)
				sizeUnsigned = Long.MAX_VALUE;
			else
				sizeUnsigned = -size;
		}
		else
			sizeUnsigned = size;

		int preIdx = 0;
		double remaining = sizeUnsigned;

		while (remaining > base && (preIdx + 1) < postfixe.length)
		{
			++preIdx;
			remaining /= base;
		}
		return nf.format(remaining) + postfixe[preIdx];
	}

	private static Map fontRenderHints_;

	/**
	 * Adds the font render hints from guest desktop.
	 */
	public static void addDesktopFontHints(Graphics2D g2)
	{
		Toolkit tk = Toolkit.getDefaultToolkit();

		if (fontRenderHints_ == null)
		{
			fontRenderHints_ = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
			if (fontRenderHints_ == null)
			{
				fontRenderHints_ = new HashMap();
				fontRenderHints_.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			}
		}
		g2.addRenderingHints(fontRenderHints_);
	}

	/**
	 * Gets the paint as string.<br>
	 * E.g. a color as RGB: "R,G,B"
	 */
	public static String paintToString(Paint paint)
	{
		if (paint == null)
			return "";

		if (paint instanceof Color)
		{
			Color col = (Color) paint;
			StringBuilder sb = new StringBuilder(20);
			sb.append(col.getRed())
			  .append(",");
			sb.append(col.getGreen())
			  .append(",");
			sb.append(col.getBlue());
			return sb.toString();
		}
		else if (paint instanceof TexturePaint)
		{
			TexturePaint t = (TexturePaint) paint;
			return ImageTool.getImageName(t.getImage());
		}
		else
			return paint.toString();
	}
}