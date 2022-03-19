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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * List of related filters.
 */
public class FilterChain
{
	private List<FilterBase> filters_;

	public FilteredImage render( PainterBuffers buffers, double scaleX, double scaleY )
	{
		FilteredImage result = new FilteredImage();
		result.offset_= new Point2D.Double(0,0);
		for ( FilterBase f : filters_)
		{
			List<BufferedImage> src = f.getSourceBuffers(buffers);
			if ( !src.isEmpty() )
			{
				Dimension d = f.getTargetDimension(src, scaleX, scaleY);
				result.image_ = buffers.getTargetBuffer(f.target_, d.width, d.height);
				Point2D.Double off = f.getOffset(scaleX, scaleY);
				result.offset_.x += off.x;
				result.offset_.y += off.y;
				f.render(buffers, f.target_, src, result.image_, scaleX, scaleY);
			}
		}
		return result;
	}

	/**
	 * Create a chain of filters.
	 */
	public FilterChain(List<FilterBase> filter)
	{
		filters_ = new ArrayList<>(filter);
	}
}
