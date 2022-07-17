/*
 *  (c) copyright 2022 Bernd Wengenroth
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 * 
 */
package com.bw.jtools.geometry;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ConvexHull
{
	private static long cross(Point O, Point A, Point B)
	{
		return (A.x - O.x) * (long) (B.y - O.y) - (A.y - O.y) * (long) (B.x - O.x);
	}

	public static <T extends Point> Point[] convex_hull_graham_andrew(Collection<T> P)
	{
		return convex_hull_graham_andrew(P.toArray(new Point[P.size()]));
	}

	public static <T extends Point> Point[] convex_hull_graham_andrew(T[] P)
	{
		Point[] H;

		if (P.length > 1)
		{
			int n = P.length;
			int k = 0;
			H = new Point[2 * n];

			Arrays.sort(P, (T p1, T p2) ->
			{
				final int xd = p1.x - p2.x;
				if (xd == 0)
				{
					return p1.y - p2.y;
				}
				else
				{
					return xd;
				}
			});

			// lower hull
			for (T value : P)
			{
				while (k >= 2 && cross(H[k - 2], H[k - 1], value) <= 0)
					k--;
				H[k++] = value;
			}

			// upper hull
			for (int i = n - 2, t = k + 1; i >= 0; i--)
			{
				while (k >= t && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
			if (k > 1)
			{
				H = Arrays.copyOfRange(H, 0, k - 1);
			}
		}
		else
			H = P;

		return H;
	}

	private static double cross2D(Point2D O, Point2D A, Point2D B)
	{
		return (A.getX() - O.getX()) * (long) (B.getY() - O.getY()) - (A.getY() - O.getY()) * (long) (B.getX() - O.getX());
	}

	public static <T extends Point2D> List<T> convex_hull_graham_andrew_2D(Collection<T> P)
	{
		return (List<T>) convex_hull_graham_andrew_2D(P.toArray(new Point2D[P.size()]));
	}

	public static <T extends Point2D> List<T> convex_hull_graham_andrew_2D(T[] P)
	{
		Point2D[] H;

		if (P.length > 1)
		{
			int n = P.length;
			int k = 0;
			H = new Point2D[2 * n];

			Arrays.sort(P, (T p1, T p2) ->
			{
				final double xd = p1.getX() - p2.getX();
				if (xd == 0)
				{
					return (int) (p1.getY() - p2.getY());
				}
				else
				{
					return (int) xd;
				}
			});

			// lower hull
			for (int i = 0; i < n; ++i)
			{
				while (k >= 2 && cross2D(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}

			// upper hull
			for (int i = n - 2, t = k + 1; i >= 0; i--)
			{
				while (k >= t && cross2D(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
			if (k > 1)
			{
				H = Arrays.copyOfRange(H, 0, k - 1);
			}
		}
		else
			H = P;

		return (List<T>) Arrays.asList(H);
	}

}
