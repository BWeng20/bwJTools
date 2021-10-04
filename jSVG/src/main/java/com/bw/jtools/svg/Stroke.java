package com.bw.jtools.svg;

import java.awt.BasicStroke;

public class Stroke
{
	public Stroke(
			Color color,
			Double width,
			float[] dasharray,
			Double dashoffset,
			LineCap linecap,
			LineJoin linejoin,
			Double miterlimit)
	{
		int cap = BasicStroke.CAP_BUTT;
		if (linecap != null)
			switch (linecap)
			{
				case butt:
					break;
				case round:
					cap = BasicStroke.CAP_ROUND;
					break;
				case square:
					cap = BasicStroke.CAP_SQUARE;
					break;
			}

		int join = BasicStroke.JOIN_MITER;
		if (linejoin != null)
			switch (linejoin)
			{
				case bevel:
					join = BasicStroke.JOIN_BEVEL;
					break;
				case round:
					join = BasicStroke.JOIN_ROUND;
					break;
				case arcs:
				case miter:
				case miter_clip:
					break;
			}

		stroke_ = new BasicStroke(width == null ? 1f : width.floatValue(), cap, join,
				miterlimit == null ? 4f : miterlimit.floatValue(), dasharray,
				dashoffset == null ? 0f : dashoffset.floatValue());
		paint_ = color == null ? null : color.getPaintWrapper();
		opacity_ = color == null ? 1f : color.getOpacity();
	}

	private java.awt.Stroke stroke_;
	private PaintWrapper paint_;
	private float opacity_;

	public java.awt.Stroke getStroke()
	{
		return stroke_;
	}

	public PaintWrapper getPaintWrapper()
	{
		return paint_;
	}

	public float getOpacity()
	{
		return opacity_;
	}
}
