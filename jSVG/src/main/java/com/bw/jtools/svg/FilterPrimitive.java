/*
 * (c) copyright 2021 Bernd Wengenroth
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

package com.bw.jtools.svg;

import java.awt.MultipleGradientPaint;
import java.util.ArrayList;
import java.util.List;

/**
 * Base for SVG Filter Primitive implementations.
 */
public abstract class FilterPrimitive
{
	/** Default for x value of region definitions. */
	public static final Length xDefault = new Length(0, LengthUnit.percent);
	/** Default for y value of region definitions. */
	public static final Length yDefault = new Length(0, LengthUnit.percent);
	/** Default for width value of region definitions. */
	public static final Length widthDefault = new Length(100, LengthUnit.percent);
	/** Default for height value of region definitions. */
	public static final Length heightDefault = new Length(100, LengthUnit.percent);

	/**
	 * SVG tag of the filter primitive.
	 */
	public final Type type_;

	/**
	 * The color interpolation from attribute "color-interpolation-filters".
	 */
	public MultipleGradientPaint.ColorSpaceType colorInterpolation_;

	/**
	 * The effective region of the filter.
	 */
	public Length x_, y_, width_, height_;

	/** Names of source buffers. */
	public final List<String> in_ = new ArrayList<>();
	/** Name of result buffer. */
	public String result_;

	/** Get number of input buffers. */
	public abstract int numberOfInputs();

	/** Check if a SVG tag is a filter primitive. */
	public static boolean isFilterPrimitive(Type type)
	{
		return type.ordinal() >= Type.feBlend.ordinal();
	}

	protected FilterPrimitive(Type filterType)
	{
		type_ = filterType;
	}
}
