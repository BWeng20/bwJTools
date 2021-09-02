package com.bw.jtools.ui.vector.svg;

import com.bw.jtools.Log;

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
				Log.warn("Unknown line-join mode "+val);
			}
		}
		return miter;
	}
}
