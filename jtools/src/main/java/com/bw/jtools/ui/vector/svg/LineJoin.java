package com.bw.jtools.ui.vector.svg;

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
			}
		}
		return miter;
	}
}
