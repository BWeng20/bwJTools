package com.bw.jtools.shape.svg;

public enum LineJoin
{
	arcs, bevel, miter, miter_clip, round;

	public static LineJoin fromString(String val)
	{
		if (val != null)
		{
			try
			{
				return LineJoin.valueOf(val.replace('-', '_')
										   .toLowerCase());
			}
			catch (IllegalArgumentException i)
			{
				SVGConverter.warn("Unknown line-join mode " + val);
			}
		}
		return miter;
	}
}
