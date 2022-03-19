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
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RadialGradient extends Gradient
{
	public Length cx, cy, r, fx, fy, fr;

	/**
	 * Default value for all properties is 50%.
	 */
	private static final Length default_ = new Length(50, LengthUnit.percent);
	;

	public RadialGradient(String id)
	{
		super(id);
	}

	@Override
	public Paint createPaint(ElementWrapper w)
	{
		try
		{
			AffineTransform eat = new AffineTransform();
			if (aft_ != null) eat.setTransform(aft_);

			final boolean userSpace = gradientUnit_ == Unit.userSpaceOnUse;
			final ShapeHelper shape = w.getShape();

			// Use 1x1 as 100% for objectBoundingBox.
			final Rectangle2D space = userSpace ? w.getViewPort() : objectBoundingBoxSpace_;
			final double spaceW = space.getWidth();
			final double spaceH = space.getHeight();

			// Raw center coordinates
			final Length ex = (cx == null ? default_ : cx);
			final Length ey = (cy == null ? default_ : cy);

			// Adapt center
			final Point2D center = new Point2D.Double(ex.toPixel(spaceW), ey.toPixel(spaceH));
			// Adapt focus, default is center.
			final Point2D focus = new Point2D.Double((fx == null ? ex : fx).toPixel(spaceW), (fy == null ? ey : fy).toPixel(spaceH));
			// Adapt radius. Default is also 50%
			// @TODO: Clarify: max to use, average, min, max?
			final float radius = (float) (r == null ? default_ : r).toPixel(Math.max(spaceW, spaceH));

			if (!userSpace)
			{
				// For objectBoundingBox adapt coordinate space
				Rectangle2D box = shape.getBoundingBox();
				// 2. Scale
				eat.preConcatenate(AffineTransform.getScaleInstance(box.getWidth(), box.getHeight()));
				// 1. Move
				eat.preConcatenate(AffineTransform.getTranslateInstance(box.getX(), box.getY()));
			}

			return new RadialGradientPaint(center, radius, focus,
					getFractionsArray(), getColorArray(w.effectiveOpacity()),
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					MultipleGradientPaint.ColorSpaceType.SRGB,
					eat);
		}
		catch (Exception e)
		{
			SVGConverter.error(e, "Failed to create radialGradient %s", id_);
			return null;
		}
	}

	public void copyFromTemplate(Gradient template)
	{
		super.copyFromTemplate(template);
		if (template instanceof RadialGradient)
		{
			RadialGradient rg = (RadialGradient) template;

			if (cx == null) cx = rg.cx;
			if (cy == null) cy = rg.cy;
			if (r == null) r = rg.r;
			if (fx == null) fx = rg.fx;
			if (fy == null) fy = rg.fy;
			if (fr == null) fr = rg.fr;
		}
	}
}
