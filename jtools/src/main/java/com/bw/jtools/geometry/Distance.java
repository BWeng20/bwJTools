package com.bw.jtools.geometry;

import java.awt.*;
import java.awt.geom.Point2D;

public final class Distance
{
	public static double euclidean(Point p1, Point p2)
	{
		final long dx = p1.x - p2.x;
		final long dy = p1.y - p2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static long squared(Point p1, Point p2)
	{
		final long dx = p1.x - p2.x;
		final long dy = p1.y - p2.y;
		return dx * dx + dy * dy;
	}

	public static double euclidean(Point2D p1, Point2D p2)
	{
		final double dx = p1.getX() - p2.getX();
		final double dy = p1.getY() - p2.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static double squared(Point2D p1, Point2D p2)
	{
		final double dx = p1.getX() - p2.getX();
		final double dy = p1.getY() - p2.getY();
		return dx * dx + dy * dy;
	}
}
