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

import com.bw.jtools.ui.UITool;

import java.awt.Color;
import java.awt.Paint;

/**
 * Convenience wrapper for a property with Paint content.
 */
public class PropertyPaintValue extends PropertyValue<Paint>
{
	/**
	 * Variable serialVersionUID of long
	 */
	private static final long serialVersionUID = 8880872805669539218L;

	/**
	 * Creates a "paint"-value with name and value.
	 *
	 * @param key   Key of the property.
	 * @param value The value. Can be null.
	 */
	public PropertyPaintValue(String key, Paint value)
	{
		super(key, Paint.class);
		setValue(value);
	}

	/**
	 * Convenience method to cast the paint to color.
	 */
	public Color getColorValue()
	{
		return (Color) getValue();
	}

	/**
	 * Gets the paint value as string. E.g. a color as RGB: "R,G,B"
	 */
	public String toString()
	{
		return UITool.paintToString(getValue());
	}

}
