package com.bw.jtools.svg;

/**
 * Encapsulates color and gradients definitions to adapt gradients if needed.
 * Zhe class is invariant. Any modification creates a new instance.
 */
public final class PaintWrapper
{
	private Gradient gradient_;
	private java.awt.Color color_;

	private PaintWrapper()
	{
	}

	public PaintWrapper(java.awt.Color color)
	{
		color_ = color;
	}

	public PaintWrapper(Gradient gradient)
	{
		gradient_ = gradient;
	}

	public PaintWrapper adaptOpacity(float opacity)
	{
		if (opacity == 1f)
			return this;

		PaintWrapper pw = new PaintWrapper();
		if (color_ != null)
			pw.color_ = Color.adaptOpacity(color_, opacity);
		if (gradient_ != null)
			pw.gradient_ = gradient_.adaptOpacity(opacity);
		return pw;
	}

	public java.awt.Color getColor()
	{
		return color_;
	}

	public java.awt.Paint createPaint(ElementWrapper w)
	{
		if (gradient_ == null)
		{
			if (color_ != null)
			{
				double opacity = w.effectiveOpacity();
				return (opacity == 1f) ? color_ : Color.adaptOpacity(color_, opacity);
			}
			return color_;
		}
		else
			return gradient_.createPaint(w);
	}

}
