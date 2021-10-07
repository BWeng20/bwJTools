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

	/** Default value for all properties is 50%. */
	private static final Length default_ = new Length(50, LengthUnit.percent );;

	public RadialGradient(String id)
	{
		super(id, "radialGradient");
	}

	@Override
	public Paint createPaint(ElementWrapper w)
	{
		try
		{
			AffineTransform eat = getEffectiveTransform(w);

			final boolean userSpace = gradientUnits_ == GradientUnits.userSpaceOnUse;
			final ShapeHelper shape = w.getShape();

			// Raw center coordinates
			final Length ex = (cx==null?default_:cx);
			final Length ey = (cy==null?default_:cy);

			// Use 1 as 100% for userSpaceOnUse
			final Double abs = userSpace ? null : 1d;
			// Adapt center
			final Point2D center = new Point2D.Double(ex.toPixel(abs), ey.toPixel(abs) );
			// Adapt focus, default is center.
			final Point2D focus = new Point2D.Double((fx==null?ex:fx).toPixel(abs),(fy==null?ey:fy).toPixel(abs) );
			// Adapt radius. Default is again 50%
			final float radius = (float) (r==null?default_:r).toPixel(abs);

			if ( !userSpace) {
				// For objectBoundingBox adapt coordinate space
				Rectangle2D box = shape.getBoundingBox();
				// 2. Scale
				eat.preConcatenate( AffineTransform.getScaleInstance(box.getWidth(), box.getHeight()) );
				// 1. Move
				eat.preConcatenate( AffineTransform.getTranslateInstance(box.getX(), box.getY()) );
			}

			RadialGradientPaint rg = new RadialGradientPaint( center, radius, focus,
					getFractionsArray(), getColorArray(),
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					MultipleGradientPaint.ColorSpaceType.SRGB,
					eat);

			return rg;
		}
		catch (Exception e)
		{
			SVGConverter.error("Failed to create radialGradient '" + id_ + "'", e);
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
