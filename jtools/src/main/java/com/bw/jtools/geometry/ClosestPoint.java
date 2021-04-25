package com.bw.jtools.geometry;

import java.awt.*;

public class ClosestPoint
{

	/**
	 * Get closest point from p1 to line segment lp1,lp2
	 */
	public static Point onLineSegment(Point p1, Point lp1, Point lp2)
	{
		int A = p1.x - lp1.x;
		int B = p1.y - lp1.y;
		int C = lp2.x - lp1.x;
		int D = lp2.y - lp1.y;

		long dot = A * C + B * D;
		long len_sq = C * C + D * D;
		double param = -1;
		if (len_sq != 0)
			param = dot / len_sq;

		Point result;
		int xx, yy;

		if (param < 0)
		{
			result = new Point(lp1);
		}
		else if (param > 1)
		{
			result = new Point(lp2);
		}
		else
		{
			result = new Point((int) (0.5 + lp1.x + param * C), (int) (0.5 + lp1.y + param * D));
		}
		return result;
	}

	/**
	 * Get closest point from p1 to path p2.
	 * p2 have to contain at least one point.
	 */
	public static Point onPath(Point p1, Point... p2)
	{

		Point best = null;
		if (p2.length > 0)
		{
			best = p2[0];
			if (p2.length > 1)
			{
				long bestD = Distance.squared(p1, best);

				for (int i = 1; i < p2.length; ++i)
				{
					Point candidate = onLineSegment(p1, p2[i - 1], p2[i]);
					final long d = Distance.squared(p1, candidate);
					if (d < bestD)
					{
						bestD = d;
						best = candidate;
					}
				}
			}
		}
		return best;
	}
}
