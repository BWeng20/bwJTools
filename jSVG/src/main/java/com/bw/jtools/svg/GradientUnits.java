package com.bw.jtools.svg;

import java.util.HashMap;
import java.util.Map;

public enum GradientUnits
{
	userSpaceOnUse,
	objectBoundingBox;

	private static final Map<String, GradientUnits> lowerCaseMap_;

	static
	{
		lowerCaseMap_ = new HashMap<>();
		for (GradientUnits gu : GradientUnits.values())
			lowerCaseMap_.put(gu.name()
								.toLowerCase(), gu);
	}


	public static GradientUnits fromString(String val)
	{
		if (val != null)
			return lowerCaseMap_.get(val.trim()
										.toLowerCase());
		return null;
	}

}
