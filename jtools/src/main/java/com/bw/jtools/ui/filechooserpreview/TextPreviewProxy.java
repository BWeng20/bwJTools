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

import com.bw.jtools.Log;
import com.bw.jtools.ui.I18N;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Helper class to track the loading state of a preview for text files.<br>
 */
class TextPreviewProxy extends PreviewProxy
{
	public long lastReadPos_ = 0;
	public int orgTextMaxLength_ = -1;
	public int orgTextMaxLines_= -1;

	public String textContent_;
	public int textMaxLength_;
	public int textMaxLines_;

	private final TextPreviewHandler handler_;

	public TextPreviewProxy( TextPreviewHandler handler)
	{
		handler_ = handler;
	}

	public void setPreviewRows(int visibleRows)
	{
		if ( textMaxLines_ != visibleRows )
		{
			textMaxLength_ = visibleRows * 1024;
			textMaxLines_ = visibleRows;
			if (textContent_ != null)
			{
				textContent_ = null;
				complete = false;
			}
		}
	}

	@Override
	public boolean needsUpdate()
	{
		setPreviewRows( handler_.previewText_.getVisibleRows() );
		return (complete) &&
				( orgTextMaxLength_ != textMaxLength_ || orgTextMaxLines_ != textMaxLines_ );
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(100);
		if (textContent_ != null)
			sb.append(" Text:")
			  .append(textContent_, 0, Math.min(10, textContent_.length()));
		return sb.toString();
	}



}
