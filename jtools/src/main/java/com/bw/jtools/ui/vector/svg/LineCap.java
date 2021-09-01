package com.bw.jtools.ui.vector.svg;

public enum LineCap
{
	butt,
	round,
	square;

	public static LineCap fromString(String val)
	{
		if (val != null)
		{
			try
			{
				return LineCap.valueOf(val.toLowerCase());
			}
			catch (IllegalArgumentException i)
			{
			}
		}
		return butt;
	}

}
