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

package com.bw.jtools.shape.filter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Combination of two input images using a Porter-Duff compositing operator.
 */
public class Composite extends FilterBaseSingleSource
{
	public CompositeOperator operator_;
	public List<Double> k_;

	@Override
	protected void render(PainterBuffers buffers, String targetName, BufferedImage src, BufferedImage target, double scaleX, double scaleY)
	{
		Graphics2D g2d = (Graphics2D)target.getGraphics();
		try
		{
			// @TODO Implement this filter
			g2d.drawImage( src,0,0, null);

			// @TODO Removed debugging stuff if finished.
			g2d.setColor(Color.ORANGE);
			g2d.drawRect(0,0, src.getWidth(), src.getHeight());

		}
		finally
		{
			g2d.dispose();
		}
	}

	public Composite(String source, String target,
					 CompositeOperator operator,
					 List<Double> k)
	{
		super(source, target);
		operator_ = operator;
		k_ = k;
	}
}

