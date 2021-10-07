package com.bw.jtools.svg;

import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

/**
 * Abstract base of gradient types.
 */
public abstract class Gradient
{
	public final String id_;
	public final String tag_;

	public String href_;
	public AffineTransform aft_;

	public float[] fractions_;
	public java.awt.Color[] colors_;
	public float[] opacities_;
	public GradientUnits gradientUnits_;

	protected java.awt.Color[] getColorArray()
	{
		return colors_;
	}

	protected float[] getFractionsArray()
	{
		return fractions_;
	}

	public MultipleGradientPaint.CycleMethod cycleMethod_;

	public Gradient(String id, String tag)
	{
		id_ = id;
		tag_ = tag;
	}

	public void copyFromTemplate(Gradient template)
	{
		//@TODO: Aggregate or copy? Check specs!
		if (aft_ == null) aft_ = template.aft_;
		if (gradientUnits_ == null) gradientUnits_ = template.gradientUnits_;

		if (cycleMethod_ == null) cycleMethod_ = template.cycleMethod_;
		if (fractions_ == null || fractions_.length == 0)
		{
			fractions_ = template.fractions_;
			colors_ = template.colors_;
			opacities_ = template.opacities_;
		}
	}

	public GradientUnits getGradientUnits()
	{
		return gradientUnits_ == null ? GradientUnits.objectBoundingBox : gradientUnits_;
	}

	public void resolveHref(SVGConverter svg)
	{
		if (href_ != null)
		{
			Gradient hrefGradient = svg.getPaintServer(href_);
			href_ = null;
			if (hrefGradient != null)
			{
				hrefGradient.resolveHref(svg);
				copyFromTemplate(hrefGradient);
			}
		}
	}

	public PaintWrapper getPaintWrapper(SVGConverter svg)
	{
		resolveHref(svg);
		return new PaintWrapper(this);
	}

	protected AffineTransform getEffectiveTransform(ElementWrapper w)
	{
		AffineTransform eat = w == null ? null : w.transform();
		if (eat == null)
			eat = new AffineTransform();

		AffineTransform at;
		if (eat == null)
			at = new AffineTransform();
		else
			at = new AffineTransform(eat);

		if (aft_ != null)
			at.preConcatenate(aft_);

		return at;
	}

	public abstract Paint createPaint(ElementWrapper w);

}
