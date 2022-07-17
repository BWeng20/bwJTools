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

import java.awt.Component;
import java.nio.file.Path;

/**
 * Handles preview of common information.
 */
public class InfoPreviewHandler extends PreviewHandler
{

	/**
	 * TextArea to show text based content previews.
	 */
	protected TextPreview previewText_;

	/**
	 * Creates a new handler.
	 */
	protected InfoPreviewHandler()
	{
		super(null);
	}


	@Override
	protected void updatePreviewProxy(PreviewProxy proxy)
	{
	}

	@Override
	public Component getPreviewComponent(PreviewProxy proxy)
	{
		if ( previewText_ == null )
		{
			previewText_ = new TextPreview();
			previewText_.setEditable(false);
			previewText_.setDisabledTextColor(previewText_.getForeground());
		}
		synchronized (proxy)
		{
			InfoPreviewProxy infoProxy = (InfoPreviewProxy) proxy;
			if (infoProxy.complete)
				previewText_.setText(infoProxy.message_);
			else
				// MessageText....
				previewText_.setText("");
		}

		return previewText_;
	}

	@Override
	protected InfoPreviewProxy createPreviewProxy(Path file, String text)
	{
		final InfoPreviewProxy proxy = new InfoPreviewProxy(this);

		proxy.name_ = "Loading";
		proxy.complete = true;
		proxy.imageContent_ = config_.loadingImage_;
		proxy.message_ = text;

		proxy.file_ = file;
		proxy.config_ = config_;

		return proxy;
	}
}
