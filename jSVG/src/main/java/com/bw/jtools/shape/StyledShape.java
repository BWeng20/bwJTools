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

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A shape plus additional style information.
 */
public final class StyledShape extends AbstractShape
{
	public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1f);

	/**
	 * The shape.
	 */
	public final Shape shape_;

	/**
	 * The stroke to render the outline.
	 */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.<br>
	 * Can be null.
	 */
	public final Paint paint_;

	/**
	 * The Paint to fill the shape. <br>
	 * Can be null.
	 */
	public final Paint fill_;

	/**
	 * Transform to be applied to the graphics context.<br>
	 * Never null.
	 */
	public AffineTransform aft_;

	public final Shape clipping_;

	private Rectangle2D transformedBounds_;


	/**
	 * Constructor to initialize,
	 */
	public StyledShape(String id, Shape shape, Stroke stroke, Paint paint, Paint fill,
					   Shape clipping, AffineTransform aft)
	{
		super(id);
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;
		this.clipping_ = clipping;
		this.aft_ = aft == null ? new AffineTransform() : aft;
	}

	/**
	 * Get bounds of the transformed shape including stroke-width.
	 */
	@Override
	public Rectangle2D getTransformedBounds()
	{
		if (transformedBounds_ == null)
		{
			final double lw = ((stroke_ instanceof BasicStroke) ? (BasicStroke) stroke_ : DEFAULT_STROKE).getLineWidth();
			Rectangle2D r = shape_.getBounds2D();
			r = new Rectangle2D.Double(r.getX() - lw, r.getY() - lw, r.getWidth() + 2 * lw, r.getHeight() + 2 * lw);
			transformedBounds_ = aft_.createTransformedShape(r)
									 .getBounds2D();
		}
		return transformedBounds_;
	}

	protected AffineTransform aftTemp_ = new AffineTransform();


	@Override
	public void paint(Context ctx)
	{
		aftTemp_.setTransform(ctx.aft_);
		aftTemp_.concatenate(aft_);

		final Graphics2D g3D = ctx.g2D_;

		g3D.setTransform(aftTemp_);
		g3D.setClip(clipping_);

		Paint p = transatePaint(ctx, fill_);
		if (p != null)
		{
			g3D.setPaint(p);
			g3D.fill(shape_);
		}

		Composite c = g3D.getComposite();

		if (paint_ != null)
		{
			p = transatePaint(ctx, paint_);
			if (p != null)
			{
				g3D.setPaint(p);
				g3D.setStroke(stroke_);
				g3D.draw(shape_);
				g3D.setComposite(c);
			}
		}
	}

}
