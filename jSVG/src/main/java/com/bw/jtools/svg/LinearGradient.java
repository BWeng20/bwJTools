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

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LinearGradient extends Gradient
{
	public Length x1, y1, x2, y2;

	private static final Length startPointDefault_ = new Length(0, LengthUnit.px);
	private static final Length endPointDefault_ = new Length(100, LengthUnit.percent);

	public LinearGradient(String id)
	{
		super(id);
	}

	@Override
	public java.awt.Paint createPaint(ElementWrapper w)
	{
		try
		{
			final double opacity = w.effectiveOpacity();

			AffineTransform eat = new AffineTransform();
			if (aft_ != null) eat.setTransform(aft_);

			final boolean userSpace = gradientUnit_ == GradientUnit.userSpaceOnUse;
			final ShapeHelper shape = w.getShape();

			// Use 1x1 as 100% for objectBoundingBox.
			final Rectangle2D space = userSpace ? w.getViewPort() : objectBoundingBoxSpace_;
			final double spaceW = space.getWidth();
			final double spaceH = space.getHeight();

			// Raw start coordinates
			final Length startX = (x1 == null ? startPointDefault_ : x1);
			final Length startY = (y1 == null ? startPointDefault_ : y1);

			// Raw end coordinates
			final Length endX = (x2 == null ? endPointDefault_ : x2);
			final Length endY = (y2 == null ? endPointDefault_ : y2);

			// Adapt points
			final Point2D startPoint = new Point2D.Double(startX.toPixel(spaceW), startY.toPixel(spaceH));
			final Point2D endPoint = new Point2D.Double(endX.toPixel(spaceW), endY.toPixel(spaceH));

			if (!userSpace)
			{
				// For objectBoundingBox adapt coordinate space
				Rectangle2D box = shape.getBoundingBox();
				// 2. Scale
				AffineTransform scale = AffineTransform.getScaleInstance(box.getWidth(), box.getHeight());
				eat.preConcatenate(scale);
				// 1. Move
				eat.preConcatenate(AffineTransform.getTranslateInstance(box.getX(), box.getY()));
			}

			return new LinearGradientPaint(
					startPoint, endPoint,
					getFractionsArray(), getColorArray(opacity),
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					LinearGradientPaint.ColorSpaceType.LINEAR_RGB, eat);
		}
		catch (Exception e)
		{
			SVGConverter.error(e, "Failed to create linearGradient %s", id_);
			return null;
		}
	}

	@Override
	public void copyFromTemplate(Gradient template)
	{
		super.copyFromTemplate(template);
		if (template instanceof LinearGradient)
		{
			LinearGradient lg = (LinearGradient) template;
			if (x1 == null) x1 = lg.x1;
			if (y1 == null) y1 = lg.y1;
			if (x2 == null) x2 = lg.x2;
			if (y2 == null) y2 = lg.y2;
		}
	}
}
