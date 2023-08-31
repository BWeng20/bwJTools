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
package com.bw.jtools.ui.filechooserpreview;

import com.bw.jtools.shape.AbstractShape;

/**
 * Helper class to track the loading state of a svg-preview.<br>
 */
class SVGPreviewProxy extends PreviewProxy
{
	/**
	 * The converted shape.
	 */
	protected AbstractShape shape_;

	/**
	 * The original width of the image
	 */
	protected int width_ = -1;

	/**
	 * The original height of the image
	 */
	protected int height_ = -1;

	protected final SVGPreviewHandler handler_;

	public SVGPreviewProxy(SVGPreviewHandler svgPreviewHandler)
	{
		super();
		handler_ = svgPreviewHandler;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString())
		  .append(" svg:");
		return sb.toString();
	}


	@Override
	public boolean needsUpdate()
	{
		return false;
	}

	/**
	 * Explicit Clean-Up.
	 */
	@Override
	protected void dispose()
	{
		super.dispose();
		shape_ = null;
	}

}
