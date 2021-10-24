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
 * Encapsulates color and gradients definitions to adapt gradients if needed.
 * Zhe class is invariant. Any modification creates a new instance.
 */
public final class PaintWrapper
{
	private Gradient gradient_;
	private java.awt.Color color_;

	private PaintWrapper()
	{
	}

	public PaintWrapper(java.awt.Color color)
	{
		color_ = color;
	}

	public PaintWrapper(Gradient gradient)
	{
		gradient_ = gradient;
	}

	public PaintWrapper adaptOpacity(float opacity)
	{
		if (opacity == 1f)
			return this;

		PaintWrapper pw = new PaintWrapper();
		if (color_ != null)
			pw.color_ = Color.adaptOpacity(color_, opacity);
		if (gradient_ != null)
			pw.gradient_ = gradient_.adaptOpacity(opacity);
		return pw;
	}

	public java.awt.Color getColor()
	{
		return color_;
	}

	public java.awt.Paint createPaint(ElementWrapper w)
	{
		if (gradient_ == null)
		{
			if (color_ != null)
			{
				double opacity = w.effectiveOpacity();
				return (opacity == 1f) ? color_ : Color.adaptOpacity(color_, opacity);
			}
			return color_;
		}
		else
			return gradient_.createPaint(w);
	}

}
