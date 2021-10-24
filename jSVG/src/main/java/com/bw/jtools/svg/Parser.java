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

/**
 * Base class for Parser.<br>
 */
public class Parser
{
	private String content_;
	protected int idx_;
	protected int length_;

	protected Parser(String content)
	{
		setContent(content);
	}

	protected Parser()
	{
	}

	protected void setContent(String content)
	{
		content_ = content;
		idx_ = 0;
		length_ = content == null ? 0 : content.length();
	}

	/**
	 * Get next length.
	 */
	protected Length nextLengthPercentage()
	{
		double v = nextDouble(Double.NaN);
		if (Double.isNaN(v))
			return null;
		LengthUnit lu = LengthUnit.px;
		if (!isSeparator(nextChar()))
		{
			int idx1 = idx_ - 1;
			char c;
			do
			{
				c = nextChar();
			}
			while (c != 0 && !isSeparator(c));
			if (idx_ <= length_)
				lu = LengthUnit.fromString(content_.substring(idx1, idx_));
		}
		--idx_;
		return new Length(v, lu);
	}

	protected double nextAngle(double defaultValue)
	{
		double d = nextDouble(Double.NaN);
		if (d != Double.NaN)
			return Math.toRadians(d);
		else
			return defaultValue;
	}


	/**
	 * Get next double value.
	 */
	protected double nextDouble()
	{
		return nextDouble(0);
	}

	protected double nextDouble(double defaultVal)
	{
		double r = nextNumber(defaultVal);
		boolean negative = isNegative(r);
		char c = nextChar();
		if (c == '.')
		{
			int oid = idx_;
			long fract = 0;
			while (isDigit(c = nextChar()))
			{
				fract = (fract * 10) + (c - '0');
			}
			--idx_;
			if (negative)
				r -= fract / Math.pow(10, idx_ - oid);
			else
				r += fract / Math.pow(10, idx_ - oid);
		}
		else
			--idx_;
		if (c == 'E' || c == 'e')
		{
			++idx_;
			double exp = 0;
			char sign = nextChar();
			if (sign != '-' && sign != '+')
			{
				sign = '+';
				--idx_;
			}
			while (isDigit(c = nextChar()))
				exp = (exp * 10) + (c - '0');
			if (sign == '-')
				exp = -exp;
			r = r * Math.pow(10, exp);
			--idx_;
		}

		return r;
	}

	protected boolean isDigit(char c)
	{
		return c >= '0' && c <= '9';
	}

	protected double nextNumber()
	{
		return nextNumber(0);
	}

	protected double nextNumber(double defaultVal)
	{
		consumeSeparators();
		boolean negative = false;
		char c = nextChar();
		if (c == '-')
			negative = true;
		else if (c == 0)
			return defaultVal;
		else if (c != '+')
			--idx_;
		double val = 0;
		int digits = 0;
		while (isDigit(c = nextChar()))
		{
			val = (val * 10) + (c - '0');
			++digits;
		}
		--idx_;
		if (negative)
			val = -val;
		return (digits == 0) ? defaultVal : val;
	}

	public static boolean isSeparator(char c)
	{
		switch (c)
		{
			case 0x09:
			case 0x20:
			case 0x0A:
			case 0x0C:
			case 0x0D:
			case ',':
				return true;
			default:
				return false;
		}
	}

	protected void consumeSeparators()
	{
		while (isSeparator(nextChar())) ;
		--idx_;
	}

	protected char nextChar()
	{
		if (idx_ < length_)
			return content_.charAt(idx_++);
		else
		{
			++idx_;
			return 0;
		}
	}

	private static final long negativeZeroBits = Double.doubleToRawLongBits(-0d);

	public static boolean isNegative(double v)
	{
		return v < 0 || (v == 0d && Double.doubleToRawLongBits(v) == negativeZeroBits);
	}
}
