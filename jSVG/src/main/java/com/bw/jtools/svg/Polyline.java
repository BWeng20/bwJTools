/*
 * (c) copyright 2021 Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bw.jtools.svg;

import java.awt.geom.Path2D;

public class Polyline extends Parser
{
	public Polyline(String points)
	{
		super(points);

		Double x, y;
		boolean first = true;
		do
		{
			x = nextDouble(Double.NaN);
			y = nextDouble(Double.NaN);
			if (Double.isNaN(x) || Double.isNaN(y))
				break;
			if (first)
			{
				path_.moveTo(x, y);
				first = false;
			}
			else
				path_.lineTo(x, y);
		} while (true);
	}

	Path2D.Double path_ = new Path2D.Double();

	public Path2D getPath()
	{
		return path_;
	}

	public Path2D toPolygon()
	{
		path_.closePath();
		return path_;
	}
}
