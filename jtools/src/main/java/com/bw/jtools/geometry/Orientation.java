package com.bw.jtools.geometry;

import java.awt.*;

public enum Orientation
{
	CLOCKWISE,
	COUNTER_CLOCKWISE,
	COLLINEAR;

	public static boolean isCCW(Point a, Point b, Point r) {
		return ((b.y - a.y) * (r.x - a.x)) < ((b.x - a.x) * (r.y - a.y));
	}

	public static boolean isCW(Point a, Point b, Point r) {
		return ((b.y - a.y) * (r.x - a.x)) > ((b.x - a.x) * (r.y - a.y));
	}

	public static boolean isCL(Point a, Point b, Point r) {
		return ((b.y - a.y) * (r.x - a.x)) == ((b.x - a.x) * (r.y - a.y));
	}

	public static Orientation calculate(Point a, Point b, Point r)
	{
		final int val = ((b.y - a.y) * (r.x - a.x)) - ((b.x - a.x) * (r.y - a.y));

		if (val == 0) return COLLINEAR;
		return (val > 0) ? CLOCKWISE : COUNTER_CLOCKWISE;
	}
}
