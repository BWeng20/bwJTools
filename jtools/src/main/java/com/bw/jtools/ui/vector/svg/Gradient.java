package com.bw.jtools.ui.vector.svg;

import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

public abstract class Gradient extends ElementBase
{
	public final String id_;
	public final String tag_;

	public String href_;
	public AffineTransform aft_;

	public float[] fractions_;
	public java.awt.Color[] colors_;

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

		if (cycleMethod_ == null) cycleMethod_ = template.cycleMethod_;

		if (fractions_ == null || fractions_.length == 0)
		{
			fractions_ = template.fractions_;
			colors_ = template.colors_;
		}

	}

	public void resolveHref(SVG svg)
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

	public Paint getPaint(SVG svg)
	{
		resolveHref(svg);
		return createPaint();
	}

	@Override
	public void toSVG(StringBuilder sb)
	{
		sb.append('<')
		  .append(tag_);
		appendAttribute(sb, "id", id_);
		if (ElementWrapper.isNotEmpty(href_))
			appendAttribute(sb, "href", "#" + href_);
		if (aft_ != null)
			appendTransform(sb, "gradientTransform", aft_);
		addSVGAttributes(sb);
		sb.append('>');

		sb.append("</")
		  .append(tag_)
		  .append('>');
	}

	protected abstract Paint createPaint();

	protected abstract void addSVGAttributes(StringBuilder sb);

}
