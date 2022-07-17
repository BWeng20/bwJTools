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
package com.bw.jtools.ui;

import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * A border to display card/tile-like panels.<br>
 * The background color of the panel should diff from the background color of its parent, otherwise
 * usage of this border is meaningless and a line border is a better solution.
 */
public class JCardBorder implements Border
{
	private final int cornerWidth_;

	private final Insets insets_;

	private final RenderingHints hints_;
	private final float colorFactor_;
	private final Stroke stroke_;
	private final Stroke borderStroke_;

	public JCardBorder(float colorFactor)
	{
		this(colorFactor, 5);
	}

	public JCardBorder(float colorFactor, int lineWidth)
	{
		this(colorFactor, lineWidth, lineWidth, (int) (0.5 + lineWidth * 1.5), lineWidth);
	}

	public JCardBorder(float colorFactor, int lineWidthTop, int lineWidthLeft, int lineWidthBottom, int lineWidthRight)
	{
		cornerWidth_ = 20;
		colorFactor_ = colorFactor;
		stroke_ = new BasicStroke(1.5f);
		borderStroke_ = new BasicStroke(cornerWidth_ / 4);

		insets_ = new Insets(lineWidthTop, lineWidthLeft, lineWidthBottom, lineWidthRight);
		hints_ = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}


	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2 = (Graphics2D) ((Graphics2D) g).create();
		try
		{
			g2.setRenderingHints(hints_);

			Color col;
			Component p = c.getParent();
			if (p != null)
			{
				col = p.getBackground();
			}
			else
			{
				col = c.getBackground();
			}

			g2.setStroke(borderStroke_);
			g2.setColor(col);
			--width;
			--height;
			g2.drawRect(x, y, width, height);
			g2.setStroke(stroke_);

			int red = col.getRed();
			int green = col.getGreen();
			int blue = col.getBlue();

			float f = colorFactor_;
			for (int i = 0; i < 6; ++i)
			{
				int i2 = i << 1;
				g2.setColor(new Color((int) (red * f), (int) (green * f), (int) (blue * f)));
				g2.drawRoundRect(x + i, y + i, width - i2, height - i2, cornerWidth_ - i2, cornerWidth_ - i2);
				f = f * f;
			}
		}
		finally
		{
			g2.dispose();
		}
	}

	@Override
	public Insets getBorderInsets(Component c)
	{
		return insets_;
	}

	@Override
	public boolean isBorderOpaque()
	{
		return true;
	}
}
