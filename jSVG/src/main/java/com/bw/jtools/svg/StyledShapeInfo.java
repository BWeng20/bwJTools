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

import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * Collects all information about a shape that are needed to produce a final shape.
 */
public final class StyledShapeInfo extends ElementInfo
{
	/**
	 * The shape.
	 */
	public Shape shape_;

	/**
	 * The stroke to render the outline.
	 */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.</br>
	 * Can be null.
	 */
	public PaintWrapper paintWrapper_;


	/**
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public PaintWrapper fillWrapper_;

	/**
	 * Transform to be applied to the graphics context.
	 */
	public AffineTransform aft_;

	public Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public StyledShapeInfo(Shape shape, Stroke stroke, PaintWrapper paint, PaintWrapper fill, Shape clipping )
	{
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paintWrapper_ = paint;
		this.fillWrapper_ = fill;
		this.clipping_ = clipping;
	}

	@Override
	public void applyTransform(AffineTransform aft)
	{
		if (aft_ == null)
			aft_ = new AffineTransform(aft);
		else
			aft_.preConcatenate(aft);

	}
}
