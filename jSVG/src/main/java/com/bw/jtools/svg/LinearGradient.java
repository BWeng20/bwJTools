package com.bw.jtools.svg;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;

public class LinearGradient extends Gradient
{
	public Length x1, y1, x2, y2;

	public LinearGradient(String id)
	{
		super(id, "linearGradient");
	}

	@Override
	public java.awt.Paint createPaint(ElementWrapper w)
	{
		try
		{
			AffineTransform eat = getEffectiveTransform(w);

			ShapeHelper shape = w.getShape();

			boolean userSpace = (gradientUnits_ == GradientUnits.userSpaceOnUse);

			return new LinearGradientPaint(
					shape.getPoint(x1, y1, userSpace), shape.getPoint(x2, y2, userSpace),
					getFractionsArray(), getColorArray(),
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					LinearGradientPaint.ColorSpaceType.LINEAR_RGB, eat);
		}
		catch (Exception e)
		{
			SVGConverter.error("Failed to create linearGradient '" + id_ + "'", e);
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
