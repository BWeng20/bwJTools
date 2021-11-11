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

package com.bw.jtools.svg.css;

/**
 * Helper to simplify calculation of CSS Selector Specificity.
 */
public final class Specificity
{
	private int A;
	private int B;
	private int C;

	public static final Specificity MAX;
	public static final Specificity MIN;

	static
	{
		MAX = new Specificity();
		MAX.A = Integer.MAX_VALUE;
		MIN = new Specificity();
		MIN.A = Integer.MIN_VALUE;
	}

	public Specificity()
	{
	}

	public Specificity(Specificity other)
	{
		setTo(other);
	}

	public void setTo(Specificity other)
	{
		A = other.A;
		B = other.B;
		C = other.C;
	}

	/**
	 * Checks if more of equal.<br>
	 * For two selectors with same specificity th elater definition should be used.
	 */
	public boolean isMoreSpecificOrEqual(Specificity other)
	{
		return (A > other.A) || ((A == other.A) && ((B > other.B) || ((B == other.B) && (C >= other.C))));
	}

	public void addIdMatch()
	{
		if (A < Integer.MAX_VALUE)
			++A;
	}

	public void addClassMatch()
	{
		++B;
	}

	public void addAttributeMatch()
	{
		++B;
	}

	public void addPseudoClassMatch()
	{
		++B;
	}

	public void addTagMatch()
	{
		++C;
	}

	public void addPseudoElementMatch()
	{
		++C;
	}
}
