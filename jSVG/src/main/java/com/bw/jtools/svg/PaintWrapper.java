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

import com.bw.jtools.shape.AbstractShape;

/**
 * Encapsulates color and gradients definitions to adapt gradients if needed.
 * Zhe class is invariant. Any modification creates a new instance.
 */
public final class PaintWrapper
{
	enum Mode
	{
		Gradient,
		Color,
		ContextFill,
		ContextStroke
	}

	private Object value_;
	private final Mode mode_;

	private PaintWrapper( Mode mode)
	{
		mode_ = mode;
	}

	public PaintWrapper(PaintWrapper other)
	{
		if ( other == null )
		{
			mode_ = Mode.Color;
			value_ = AbstractShape.NONE;
		}
		else
		{
			mode_ = other.mode_;
			value_ = other.value_;
		}
	}

	public PaintWrapper(java.awt.Color color)
	{
		mode_ = Mode.Color;
		value_ = color;
	}

	public PaintWrapper(Gradient gradient)
	{
		mode_ = Mode.Gradient;
		value_ = gradient;
	}

	public static PaintWrapper contextFill()
	{
		return new PaintWrapper(Mode.ContextFill);
	}

	public static PaintWrapper contextStroke()
	{
		return new PaintWrapper(Mode.ContextStroke);
	}

	public PaintWrapper adaptOpacity(float opacity)
	{
		if (opacity == 1f)
			return this;
		PaintWrapper pw = new PaintWrapper(mode_);
		if (mode_ == Mode.Color)
			pw.value_ = Color.adaptOpacity((java.awt.Color)value_, opacity);
		else if ( mode_ == Mode.Gradient )
			pw.value_ = ((Gradient)value_).adaptOpacity(opacity);
		else
			// @TODO: What to do for context-modes?
			pw.value_ = value_;
		return pw;
	}

	public java.awt.Color getColor()
	{
		return mode_ == Mode.Color ? (java.awt.Color)value_: null;
	}

	public java.awt.Paint createPaint(ElementWrapper w)
	{
		switch (mode_)
		{
			case Color:
				return (java.awt.Color)value_;
			case Gradient:
				return ((Gradient) value_).createPaint(w);
			case  ContextFill:
				// @TODO
				return null;
			case  ContextStroke:
				// @TODO
				return null;
		}
		return null;
	}

}
