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

public enum Orientation
{
	CLOCKWISE,
	COUNTER_CLOCKWISE,
	COLLINEAR;

	public static boolean isCCW(Point a, Point b, Point r)
	{
		return ((b.y - a.y) * (r.x - a.x)) < ((b.x - a.x) * (r.y - a.y));
	}

	public static boolean isCW(Point a, Point b, Point r)
	{
		return ((b.y - a.y) * (r.x - a.x)) > ((b.x - a.x) * (r.y - a.y));
	}

	public static boolean isCL(Point a, Point b, Point r)
	{
		return ((b.y - a.y) * (r.x - a.x)) == ((b.x - a.x) * (r.y - a.y));
	}

	public static Orientation calculate(Point a, Point b, Point r)
	{
		final int val = ((b.y - a.y) * (r.x - a.x)) - ((b.x - a.x) * (r.y - a.y));

		if (val == 0) return COLLINEAR;
		return (val > 0) ? CLOCKWISE : COUNTER_CLOCKWISE;
	}
}
