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
package com.bw.jtools.properties;

import java.awt.Font;

/**
 * Convenience wrapper for a property with Font content.
 */
public class PropertyFontValue extends PropertyValue<Font>
{
	/**
	 * Variable serialVersionUID of long
	 */
	private static final long serialVersionUID = 8880872805669539218L;

	/**
	 * Creates a "font"-value with name and value.
	 *
	 * @param key  Key of the property.
	 * @param value The value. Can be null.
	 */
	public PropertyFontValue(String key, Font value)
	{
		super(key, Font.class);
		setValue(value);
	}

	/**
	 * Gets the Font as String.
	 */
	public String toString()
	{
		return toString(getValue());
	}

	/**
	 * Gets the Font as String.
	 */
	public static String toString(Font font)
	{
		StringBuilder sb = new StringBuilder();
		if (font != null)
		{
			sb.append(font.getFontName())
			  .append('-');
			if (font.isBold())
			{
				sb.append(font.isItalic() ? "bolditalic" : "bold");
			}
			else
			{
				sb.append(font.isItalic() ? "italic" : "plain");
			}
			sb.append('-')
			  .append(font.getSize());
		}
		return sb.toString();
	}
}
