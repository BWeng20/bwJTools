package com.bw.jtools.ui.vector.svg;

import java.awt.BasicStroke;
import java.awt.Paint;

public class Stroke
{
	public Stroke(
			Color color,
			Float width,
			float[] dasharray,
			Float dashoffset,
			LineCap linecap,
			LineJoin linejoin,
			Float miterlimit)
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

		stroke_ = new BasicStroke(width == null ? 1f : width, cap, join,
				miterlimit == null ? 4f : miterlimit, dasharray,
				dashoffset == null ? 0f : dashoffset);
		paint_ = color == null ? null : color.getColor();
	}

	private java.awt.Stroke stroke_;
	private Paint paint_;

	public java.awt.Stroke getStroke()
	{
		return stroke_;
	}

	public Paint getColor()
	{
		return paint_;
	}
}
