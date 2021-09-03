package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Log;

import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class RadialGradient extends Gradient
{
	public Double cx, cy, r, fx, fy, fr;

	public RadialGradient(String id)
	{
		super(id, "radialGradient");
	}

	@Override
	public Paint createPaint()
	{
		try
		{
			return new RadialGradientPaint(
					new Point2D.Double(cx, cy), r.floatValue(), new Point2D.Double(fx, fy),
					fractions_, colors_,
					cycleMethod_ == null ? MultipleGradientPaint.CycleMethod.NO_CYCLE : cycleMethod_,
					MultipleGradientPaint.ColorSpaceType.SRGB,
					aft_ == null ? new AffineTransform() : aft_);
		}
		catch (Exception e)
		{
			Log.error("Failed to create radialGradient '" + id_ + "'", e);
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
