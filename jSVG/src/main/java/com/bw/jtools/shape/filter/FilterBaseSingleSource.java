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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Base for filters.
 */
public abstract class FilterBaseSingleSource extends FilterBase
{
	protected void render(PainterBuffers buffers, String targetName, List<BufferedImage> src, BufferedImage target, double scaleX, double scaleY )
	{
		if ( src.size() == 1)
			render(buffers, targetName, src.get(0), target, scaleX, scaleY);
	}

	protected abstract void render(PainterBuffers buffers, String targetName, BufferedImage src, BufferedImage target, double scaleX, double scaleY );

	protected Dimension getTargetDimension(List<BufferedImage> srcBuffers, double scaleX, double scaleY)
	{
		if (srcBuffers.isEmpty())
			return new Dimension(0,0);
		else
		{
			BufferedImage src = srcBuffers.get(0);
			return getTargetDimension(src.getWidth(), src.getHeight(), scaleX, scaleY);
		}
	}

	protected Dimension getTargetDimension(int srcWidth, int srcHeight, double scaleX, double scaleY)
	{
		return new Dimension(srcWidth, srcHeight);
	}

	protected FilterBaseSingleSource( String source, String target)
	{
		super(source, target);
	}
}
