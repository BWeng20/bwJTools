package com.bw.jtools.shape;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

	public void setShapes(Collection<ShapeWithStyle> shapes)
	{
		painter_.clearShapes();
		if (shapes != null)
			for (ShapeWithStyle s : shapes)
				painter_.addShape(s);
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


	public void addShape(ShapeWithStyle shape)
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
			Graphics2D g2D = (Graphics2D) g.create();
			try
			{
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (drawFrame_)
				{
					g2D.setPaint(framePaint_);
					g2D.draw(painter_.getArea());
				}
				g2D.setColor(getBackground());
				painter_.paintShapes(g2D, isOpaque());
			}
			finally
			{
				g2D.dispose();
			}
		}
	}

	private void refresh()
	{
		invalidate();
		repaint();
	}
}
