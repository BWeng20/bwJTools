package com.bw.jtools.svg;

import java.util.HashMap;
import java.util.Map;

public enum GradientUnit
{
	userSpaceOnUse,
	objectBoundingBox;

	private static final Map<String, GradientUnit> lowerCaseMap_;

	static
	{
		lowerCaseMap_ = new HashMap<>();
		for (GradientUnit gu : GradientUnit.values())
			lowerCaseMap_.put(gu.name()
								.toLowerCase(), gu);
	}


	public static GradientUnit fromString(String val)
	{
		if (val != null)
			return lowerCaseMap_.get(val.trim()
										.toLowerCase());
		return null;
	}

}
