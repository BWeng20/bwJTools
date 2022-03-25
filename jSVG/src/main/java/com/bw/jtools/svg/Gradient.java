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

import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Abstract base of gradient types.
 */
public abstract class Gradient implements Cloneable
{
	public final String id_;

	public String href_;
	public AffineTransform aft_;

	protected float[] fractions_;
	protected java.awt.Color[] colors_;
	public Unit gradientUnit_;
	public MultipleGradientPaint.CycleMethod cycleMethod_;

	/**
	 * Space for objectBoundingBox gradientUnits
	 */
	public static final Rectangle2D.Double objectBoundingBoxSpace_ = new Rectangle2D.Double(0, 0, 1d, 1d);

	/**
	 * Get the color list of this gradient.
	 *
	 * @param opacity The global opacity to adapt the colors.
	 */
	public java.awt.Color[] getColorArray(double opacity)
	{
		if (opacity == 1f)
		{
			return colors_;
		}
		else if (colors_ == null)
			return null;
		else
		{
			java.awt.Color adaptedColors[] = new java.awt.Color[colors_.length];
			for (int i = 0; i < adaptedColors.length; ++i)
				adaptedColors[i] = Color.adaptOpacity(colors_[i], opacity);
			return adaptedColors;
		}
	}

	/**
	 * Creates adapted copy if opacity != 1.
	 * Returns this instalce if opacity = 1.
	 */
	public Gradient adaptOpacity(float opacity)
	{
		if (opacity == 1f)
			return this;

		try
		{
			Gradient pw = (Gradient) clone();
			pw.colors_ = getColorArray(opacity);
			return pw;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}


	public float[] getFractionsArray()
	{
		return fractions_;
	}


	protected Gradient(String id)
	{
		id_ = id;
	}

	public void copyFromTemplate(Gradient template)
	{
		//@TODO: Aggregate or copy? Check specs!
		if (aft_ == null) aft_ = template.aft_;
		if (gradientUnit_ == null) gradientUnit_ = template.gradientUnit_;

		if (cycleMethod_ == null) cycleMethod_ = template.cycleMethod_;
		if (fractions_ == null || fractions_.length == 0)
		{
			fractions_ = template.fractions_;
			colors_ = template.colors_;
		}
	}

	public Unit getGradientUnits()
	{
		return gradientUnit_ == null ? Unit.objectBoundingBox : gradientUnit_;
	}

	public void resolveHref(SVGConverter svg)
	{
		if (href_ != null)
		{
			Gradient hrefGradient = svg.getPaintServer(href_);
			href_ = null;
			if (hrefGradient != null)
			{
				hrefGradient.resolveHref(svg);
				copyFromTemplate(hrefGradient);
			}
		}
	}

	public PaintWrapper getPaintWrapper(SVGConverter svg)
	{
		resolveHref(svg);
		return new PaintWrapper(this);
	}


	public abstract Paint createPaint(ElementWrapper w);

}
