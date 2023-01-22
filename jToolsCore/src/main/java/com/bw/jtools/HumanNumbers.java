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

package com.bw.jtools;

import java.util.Locale;

/**
 * Some helper methods to help humans to understand numbers.
 */
public final class HumanNumbers
{
	/**
	 * Gets a short SI-based english description of the number, at most with one fraction digits.
	 */
	public static String getShortSIFormat4Bytes(long number)
	{
		return getShortSIFormat4Bytes(number, Locale.ENGLISH);
	}

	/**
	 * Gets a short SI-based english description of the number of bytes,
	 * at most with one fraction digits.
	 *
	 * @param lc Locale to use for numeric part of result
	 */
	public static String getShortSIFormat4Bytes(double number, Locale lc)
	{
		if (lc == null) lc = Locale.ENGLISH;

		if (Double.isInfinite(number) || Double.isNaN(number))
			return String.format(lc, "%f", number);
		// Catch exotic -0
		if (number == 0d)
			number = 0;

		char[] postfix = { 0, 'k', 'M', 'G', 'T', 'P', 'E'};

		boolean negativ = number < 0;
		if (negativ) number = -number;

		int i = 0;
		while (number > 1000d && i < postfix.length)
		{
			++i;
			number /= 1000d;
		}

		return (i < postfix.length)
				? String.format(lc, "%.1f %cB", number, postfix[i])
				: String.format(lc, "%g", number);
	}
}
