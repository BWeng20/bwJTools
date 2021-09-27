package com.bw.jtools.svg;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class LinearGradient extends Gradient
{
	public Double x1, y1, x2, y2;

	public LinearGradient(String id)
	{
		super(id, "linearGradient");
	}

	@Override
	protected Paint createPaint()
	{
		try
		{
			return new LinearGradientPaint(
					new Point2D.Double(x1, y1), new Point2D.Double(x2, y2),
					getFractionsArray(), getColorArray(),
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					LinearGradientPaint.ColorSpaceType.SRGB,
					aft_ == null ? new AffineTransform() : aft_);
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
