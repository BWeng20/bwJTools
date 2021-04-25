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

import javax.swing.*;
import java.awt.*;

/**
 * A simple icon implementation to show a red cross as replacement for a real icon.
 * Not designed for real use. It is returned by
 * {@link IconTool#getIcon(java.lang.String) IconCache.getIcon}
 * and derived methods to indicate that the icon could not be created.
 */
public class DummyIcon implements Icon
{
	private final int width_ = 13;
	private final int height_ = 13;
	private final BasicStroke stroke_ = new BasicStroke(2f);

	/**
	 * Create a new dummy icon.
	 */
	public DummyIcon()
	{
	}

	/**
	 * Paints a red cross.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Graphics2D g2D = (Graphics2D) g.create();

		g2D.setColor(Color.WHITE);
		g2D.fillRect(x, y, width_, height_);

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(Color.RED);
		g2D.setStroke(stroke_);
		g2D.drawLine(x + 1, y + 1, x + width_ - 2, y + height_ - 2);
		g2D.drawLine(x + 1, y + height_ - 2, x + width_ - 2, y + 1);

		g2D.dispose();
	}

	@Override
	public int getIconWidth()
	{
		return width_;
	}

	@Override
	public int getIconHeight()
	{
		return height_;
	}
}
