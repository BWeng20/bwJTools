/*
 * (c) copyright Bernd Wengenroth
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
package com.bw.jtools.ui.icon;


import com.bw.jtools.ui.vector.ShapeInfo;
import com.bw.jtools.ui.vector.ShapePainter;

import javax.swing.Icon;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShapeIcon implements Icon
{
	private boolean drawFrame_ = true;
	private Paint framePaint_ = Color.BLACK;
	private final ShapePainter painter_;
	private final static AffineTransform noTransform = new AffineTransform();

	public ShapeIcon(Collection<ShapeInfo> shapes)
	{
		painter_ = new ShapePainter();
		for ( ShapeInfo s : shapes)
			addShape(s);
	}

	/**
	 * Adds a shape.
	 */
	public void addShape(ShapeInfo shape)
	{
		if ( shape.aft_ == null )
			shape.aft_ = noTransform;
		painter_.addShape( shape );
	}

	/**
	 * Draws a border inside the icon with the default stroke.
	 */
	public void setInlineBorder( boolean draw, Paint color)
	{
		drawFrame_ = draw;
		framePaint_ = color;
	}

	/**
	 * Draws a border inside the icon with the default stroke and Color.BLACK.
	 */
	public void setInlineBorder( boolean draw)
	{
		setInlineBorder( draw, Color.BLACK );
	}

	/**
	 * Sets X- and Y-Scale factor.
	 */
	public void setScale(double scaleX, double scaleY)
	{
		painter_.setScale( scaleX, scaleY );
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
		Graphics2D g2D = (Graphics2D) g.create();

		g2D.translate(x,y);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if ( drawFrame_ )
		{
			g2D.setPaint(framePaint_);
			g2D.draw(painter_.getArea());
		}
		g2D.setColor(c.getBackground());
		painter_.paintShapes(g2D, c.isOpaque());

		g2D.dispose();
	}

	@Override
	public int getIconWidth()
	{
		return (int) Math.ceil( painter_.getAreaWidth() );
	}

	@Override
	public int getIconHeight()
	{
		return (int) Math.ceil( painter_.getAreaHeight() );
	}

}
