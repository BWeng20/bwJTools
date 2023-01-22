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

package com.bw.jtools.ui;

import com.bw.jtools.HumanNumbers;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.lang.annotation.Retention;
import java.util.LinkedList;

/**
 * JComponent to show a rotated text.
 */
public class JRotatedText extends JComponent
{
	private double angle_;
	private AffineTransform at_;
	private String text_ ;

	/**
	 * Create a new text component.<br>
	 * @param text The text to show
	 * @param angleInDegree The angle in degree to rotate by.
	 */
	public JRotatedText( String text, int angleInDegree )
	{
		this.text_ = text;
		this.angle_ = Math.toRadians(angleInDegree);
		at_ = AffineTransform.getRotateInstance(angle_);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize()
	{
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		Rectangle2D r = new Rectangle2D.Float(0,0,
				fm.stringWidth(text_),fm.getHeight());
		Rectangle bounds = at_.createTransformedShape(r).getBounds();
		return new Dimension(bounds.width, bounds.height);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g.create();
		try
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			FontMetrics fm = g2d.getFontMetrics();
			int baseY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
			Rectangle2D r = new Rectangle2D.Float(0,0,
					fm.stringWidth(text_),fm.getHeight());
			Rectangle bounds = at_.createTransformedShape(r).getBounds();

			int w = getWidth()-1;
			int h = getHeight()-1;
			int x0 = 0;
			int y0 = 0;
			Border b = getBorder();
			if (b != null)
			{
				Insets i = b.getBorderInsets(this);
				x0 = i.left;
				y0 = i.top;
				h -= i.top + i.bottom;
				w -= i.left + i.right;
			}

			if ( isOpaque() )
			{
				g2d.setBackground(getBackground());
				g2d.clearRect(x0, y0, w, h);
			}
			g2d.setPaint(getForeground());
			g2d.translate(x0-bounds.getX(), y0-bounds.getY());
			g2d.rotate(angle_, 0,0);
			g2d.drawString(text_, 0, fm.getAscent());
		}
		finally
		{
			g2d.dispose();
		}
	}
}
