package com.bw.jtools.svg;

import java.util.HashMap;

public enum Type
{
	g,
	clipPath,
	path, rect, circle, ellipse,
	text, textPath,
	line, polyline, polygon,
	use, style,
	defs, linearGradient, radialGradient;

	private final static HashMap<String, Type> types_ = new HashMap<>();

	// Use map instead of "valueOf" to avoid exceptions for unknown values
	static
	{
		for (Type t : values())
			types_.put(t.name(), t);
	}

	public static Type valueFrom(String typeName)
	{
		return types_.get(typeName);
	}
}
