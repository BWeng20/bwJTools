package com.bw.jtools.ui.vector.svg;

/**
 * Base class for Parser.<br>
 */
public class Parser
{
	private String content_;
	protected int idx_;

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
	}

	/**
	 * Get next double length or percentage.
	 */
	protected double nextLengthPercentage(double absLength)
	{
		double v = nextDouble();
		if (nextChar() == '%')
			v = (v / 100d) * absLength;
		else
			--idx_;
		return v;
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
		while (isDigit(c = nextChar()))
			val = (val * 10) + (c - '0');
		--idx_;
		if (negative)
			val = -val;
		return val;
	}

	protected void consumeSeparators()
	{
		do
		{
			switch (nextChar())
			{
				case 0x09:
				case 0x20:
				case 0x0A:
				case 0x0C:
				case 0x0D:
				case ',':
					break;
				default:
					--idx_;
					return;
			}
		} while (true);
	}

	protected char nextChar()
	{
		if (idx_ < content_.length())
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
