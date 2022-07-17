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
package com.bw.jtools.shape;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.util.Collection;

/**
 * Icon that use a ShapePainter to render.
 */
public class ShapeIcon implements Icon
{
	private boolean drawFrame_ = true;
	private Paint framePaint_ = Color.BLACK;
	private final ShapePainter painter_;

	public ShapeIcon(Collection<AbstractShape> shapes)
	{
		painter_ = new ShapePainter();
		for (AbstractShape s : shapes)
			addShape(s);
	}

	/**
	 * Adds a shape.
	 */
	public void addShape(AbstractShape shape)
	{
		painter_.addShape(shape);
	}

	/**
	 * Draws a border inside the icon with the default stroke.
	 */
	public void setInlineBorder(boolean draw, Paint color)
	{
		drawFrame_ = draw;
		framePaint_ = color;
	}

	/**
	 * Draws a border inside the icon with the default stroke and Color.BLACK.
	 */
	public void setInlineBorder(boolean draw)
	{
		setInlineBorder(draw, Color.BLACK);
	}

	/**
	 * Sets X- and Y-Scale factor.
	 */
	public void setScale(double scaleX, double scaleY)
	{
		painter_.setScale(scaleX, scaleY);
	}

	/**
	 * Gets X-Scale factor.
	 */
	public double getXScale()
	{
		return painter_.getXScale();
	}

	/**
	 * Gets Y-Scale factor.
	 */
	public double getYScale()
	{
		return painter_.getYScale();
	}


	/**
	 * Paints the shapes.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Context ctx = new Context(g);
		try
		{

			ctx.currentColor_ = c.getBackground();
			ctx.g2D_.translate(x, y);
			ctx.g2D_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (drawFrame_)
			{
				ctx.g2D_.setPaint(framePaint_);
				ctx.g2D_.draw(painter_.getArea());
			}
			painter_.paintShapes(ctx, c.isOpaque());
		} finally
		{
			ctx.dispose();
		}
	}

	@Override
	public int getIconWidth()
	{
		return (int) Math.ceil(painter_.getAreaWidth());
	}

	@Override
	public int getIconHeight()
	{
		return (int) Math.ceil(painter_.getAreaHeight());
	}

	public ShapePainter getPainter()
	{
		return painter_;
	}

}
