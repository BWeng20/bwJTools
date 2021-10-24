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

import java.awt.Toolkit;

public final class Length
{
	public double value_;
	public LengthUnit unit_;

	public Length(double value, LengthUnit unit)
	{
		value_ = value;
		unit_ = unit;
	}

	private static final double pixelPerInch_;
	private static final double pixelPerPoint_;
	private static final double pixelPerCM_;
	private static final double pixelPerMM_;
	private static final double pixelPerPica_;

	// @TODO: font height
	private static final double pixelPerEM_ = 12;
	// @TODO: font x-height (height of small letters)
	private static final double pixelPerEX_ = 8;

	static
	{
		double ppi = 72;
		try
		{
			ppi = Toolkit.getDefaultToolkit()
						 .getScreenResolution();
		}
		catch (Exception ex)
		{
		}
		pixelPerInch_ = ppi;
		pixelPerPoint_ = ppi / 72d;
		pixelPerCM_ = 0.3937d * ppi;
		pixelPerMM_ = 0.03937d * ppi;
		pixelPerPica_ = ppi / 6d;
	}


	/**
	 * Conversion to pixel.
	 */
	public double toPixel(Double absValue)
	{
		switch (unit_)
		{
			case pt:
				return value_ * pixelPerPoint_;
			case px:
				return value_;
			case in:
				return value_ * pixelPerInch_;
			case cm:
				return value_ * pixelPerCM_;
			case mm:
				return value_ * pixelPerMM_;
			case pc:
				return value_ * pixelPerPica_;
			case em:
				return value_ * pixelPerEM_;
			case ex:
				return value_ * pixelPerEX_;
			case percent:
				return absValue == null ? value_ : (absValue * (value_ / 100d));
		}
		return value_;
	}
}
