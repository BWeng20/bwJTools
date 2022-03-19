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

import com.bw.jtools.ui.UITool;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

/**
 * An icon-implementation to show a filled rectangle with a small border.<br>
 * Designed to work as indicator for a color value. The default icon size is 13x13.
 */
public final class JColorIcon implements Icon
{
	/**
	 * The width of the icon area.
	 */
	protected final int width_;

	/**
	 * The height of the icon area.
	 */
	protected final int height_;

	/**
	 * The color of the inner area.
	 */
	protected Paint color_;

	/**
	 * Border is drawn if true.
	 */
	protected boolean borderPainted_ = true;

	protected final BasicStroke stroke_ = new BasicStroke(1f);

	/**
	 * Creates an Icon with size and initial color.
	 *
	 * @param width  The width of the icon to show.
	 * @param height The height of the icon to show.
	 * @param color  The color to show. Can be null.
	 */
	public JColorIcon(int width, int height, Paint color)
	{
		this.width_ = width;
		this.height_ = height;
		setColor(color);
	}

	/**
	 * Enabled or disables the border around the icon.
	 *
	 * @param border True to enable the border, false to disable it.
	 */
	public void setBorderPainted(boolean border)
	{
		if (borderPainted_ != border)
		{
			borderPainted_ = border;
		}
	}

	/**
	 * Creates a new (and empty) icon with size 13x13.
	 */
	public JColorIcon()
	{
		this.color_ = Color.WHITE;
		this.width_ = 13;
		this.height_ = 13;
	}

	/**
	 * Sets the shown color.The icon will not automatically be redrawn.
	 * Called needs to redraw the containing component if needed.<br>
	 * The border color is calculated via
	 * {@link com.bw.jtools.ui.UITool#calculateContrastColor(java.awt.Color) UITool.calculateContrastColor}.
	 *
	 * @param color The color to show.
	 */
	public void setColor(Paint color)
	{
		this.color_ = (color == null) ? Color.WHITE : color;
	}

	/**
	 * Gets the currently shown color.
	 *
	 * @return The currently set color.
	 */
	public Paint getColor()
	{
		return color_;
	}

	/**
	 * Paints the icon by drawing two rectangles.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Graphics2D g2D = (Graphics2D) g.create();

		g2D.setPaint(color_);
		g2D.fillRect(x, y, width_, height_);

		if (borderPainted_)
		{
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Paint borderColor;
			if (this.color_ instanceof Color)
			{
				Color bc = UITool.calculateContrastColor((Color) this.color_);
				if (Math.abs(UITool.calculateLumiance(bc) - UITool.calculateLumiance(c.getBackground())) < 20)
				{
					bc = (Color) this.color_;
				}
				borderColor = bc;
			}
			else
				borderColor = this.color_;
			g2D.setPaint(borderColor);
			g2D.setStroke(stroke_);
			g2D.drawRect(x, y, width_, height_);
		}
		g2D.dispose();
	}

	/**
	 * Get the currently set width of the icon.
	 *
	 * @return The current width.
	 */
	@Override
	public int getIconWidth()
	{
		return width_;
	}

	/**
	 * Get the currently set height of the icon.
	 *
	 * @return The current height .
	 */
	@Override
	public int getIconHeight()
	{
		return height_;
	}
}
