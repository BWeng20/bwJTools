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

import java.awt.*;

/**
 * Convenience wrapper for a property with Color content.
 */
public class PropertyColorValue extends PropertyValue
{
	/**
	 * Variable serialVersionUID of long
	 */
	private static final long serialVersionUID = 8880872805669539218L;

	/**
	 * Creates a "color"-value with name and value.
	 *
	 * @param name  Name of the property.
	 * @param value The value. Can be null.
	 */
	public PropertyColorValue(String name, Color value)
	{
		super(name, Color.class);
		setPayload(value);
	}

	/**
	 * Convenience getter.
	 * Same as (Color)getUserObject().
	 *
	 * @return The Color of null.
	 */
	public Color getValue()
	{
		return (Color) getPayload();
	}

	/**
	 * Convenience setter.
	 * Same as "setUserObject(v)".
	 *
	 * @param v The value to set. Can be null.
	 */
	public void setValue(Color v)
	{
		setPayload(v);
	}

	/**
	 * Convenience setter.
	 * Same as "setUserObject(new Color(v))".
	 *
	 * @param v The value to set.
	 */
	public void setValue(int v)
	{
		setPayload(new Color(v));
	}

	/**
	 * Gets the color as RGB: "R,G,B"
	 */
	public String toString()
	{
		return toString((Color) getPayload());
	}

	/**
	 * Gets the color as RGB: "R,G,B"
	 */
	public static String toString(Color col)
	{
		if (col == null)
			return "";

		StringBuilder sb = new StringBuilder(20);
		sb.append(col.getRed())
		  .append(",");
		sb.append(col.getGreen())
		  .append(",");
		sb.append(col.getBlue());
		return sb.toString();
	}
}
