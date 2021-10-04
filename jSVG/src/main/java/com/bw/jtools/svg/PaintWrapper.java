package com.bw.jtools.svg;

/**
 * Encapsulates color and gradients definitions to adapt gradients if needed.
 */
public class PaintWrapper
{
	protected Gradient gradient_;
	protected java.awt.Color color_;

	public PaintWrapper(java.awt.Color color)
	{
		color_ = color;
	}

	public PaintWrapper(Gradient gradient)
	{
		gradient_ = gradient;
	}

	public java.awt.Paint getPaint(SVGConverter svg, ElementWrapper w)
	{
		if (gradient_ == null)
			return color_;
		else
			return gradient_.createPaint(w);
	}

	public java.awt.Paint createPaint(ElementWrapper w)
	{
		if (gradient_ == null)
			return color_;
		else
			return gradient_.createPaint(w);
	}

}
