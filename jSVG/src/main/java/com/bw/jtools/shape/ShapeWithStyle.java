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

package com.bw.jtools.shape;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * A shape plus additional style information.
 */
public final class ShapeWithStyle
{
	public static final Color NONE = new Color(0,0,0,0);

	/**
	 * Id to identify the shape in the some document.
	 */
	public final String id_;

	/**
	 * The shape.
	 */
	public final Shape shape_;

	/**
	 * The stroke to render the outline.
	 */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.</br>
	 * Can be null.
	 */
	public final Paint paint_;

	/**
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public final Paint fill_;

	/**
	 * Transform to be applied to the graphics context.</br>
	 * Never null.
	 */
	public AffineTransform aft_;

	public final Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public ShapeWithStyle(String id, Shape shape, Stroke stroke, Paint paint, Paint fill, Shape clipping, AffineTransform aft)
	{
		this.id_ = id;
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;
		this.clipping_ = clipping;
		this.aft_ = aft == null ? new AffineTransform() : aft;
	}

}
