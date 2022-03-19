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

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class ShapePane extends JComponent
{
	public ShapePane()
	{
	}

	private boolean drawFrame_ = true;
	private Paint framePaint_ = Color.BLACK;
	private ShapePainter painter_ = new ShapePainter();


	/**
	 * Draws a border inside the icon with the default stroke and Color.BLACK.
	 */
	public void setInlineBorder(boolean draw)
	{
		setInlineBorder(draw, Color.BLACK);
	}

	/**
	 * Draws a border inside the icon with the default stroke.
	 */
	public void setInlineBorder(boolean draw, Paint color)
	{
		drawFrame_ = draw;
		framePaint_ = color;
		repaint();
	}

	/**
	 * Sets a new painter and (included) the shapes.
	 */
	public void setPainter(ShapePainter painter)
	{
		painter_ = painter;
		refresh();
	}

	public ShapePainter getPainter()
	{
		return painter_;
	}

	public void setShapes(Collection<AbstractShape> shapes)
	{
		painter_.clearShapes();
		painter_.addShapes(shapes);
		refresh();
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


	public void addShape(ShapeGroup shape)
	{
		painter_.addShape(shape);
		refresh();
	}

	/**
	 * Sets X- and Y-Scale factor.
	 */
	public void setScale(double scaleX, double scaleY)
	{
		painter_.setScale(scaleX, scaleY);
		refresh();
	}


	@Override
	public Dimension getPreferredSize()
	{
		Rectangle2D.Double area = painter_.getArea();
		return new Dimension((int) (0.5 + area.x + area.width), (int) (0.5 + area.y + area.height));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (painter_ == null)
			super.paintComponent(g);
		else
		{
			Context ctx = new Context(g);
			ctx.currentColor_ = getForeground();
			ctx.currentBackground_ = getBackground();
			try
			{
				ctx.g2D_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (drawFrame_)
				{
					ctx.g2D_.setPaint(framePaint_);
					ctx.g2D_.draw(painter_.getArea());
				}
				painter_.paintShapes(ctx, isOpaque());
			}
			finally
			{
				ctx.dispose();
			}
		}
	}

	private void refresh()
	{
		invalidate();
		repaint();
	}
}
