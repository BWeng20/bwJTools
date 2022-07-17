package com.bw.jtools.ui;

import javax.swing.JViewport;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * A viewport that paint its background with a custom paint.<br>
 * Can be used to paint a scroll-pane with a gradient or texture-paint.
 */
public class JPaintViewport extends JViewport
{
	protected Paint paint_;

	public JPaintViewport()
	{
	}

	public void setBackgroundImage(BufferedImage img)
	{
		setBackgroundPaint(
				new TexturePaint(img, new Rectangle(0, 0, img.getWidth(), img.getHeight())));
	}

	public void setBackgroundPaint(Paint paint)
	{
		paint_ = paint;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (paint_ == null)
			super.paintComponent(g);
		else
		{
			if (isOpaque())
			{
				Graphics2D g2 = (Graphics2D) g;
				g2.setPaint(paint_);
				g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
			}
		}
	}

}
