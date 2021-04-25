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

import com.bw.jtools.io.IOTool;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles preview of text files.
 */
public class TextPreviewHandler extends PreviewHandler
{
	/**
	 * Creates a new handler.
	 */
	protected TextPreviewHandler()
	{
		// File name pattern for supported text-files.
		super("(?i).*\\.(txt|text|xml|html|htm|js|cmd|sh|sql)");
	}

	/**
	 * Sets the length of the preview text.
	 * Default is 1024.
	 */
	public void setPreviewTextLength(int length)
	{
		previewTextLength_ = length;
	}

	/**
	 * Gets the length of the preview text.
	 */
	public int getPreviewTextLength()
	{
		return previewTextLength_;
	}

	/**
	 * Length of text to load for preview.
	 */
	protected int previewTextLength_ = 1024;

	/**
	 * Swing worker to load the text.
	 */
	protected class TextLoader extends SwingWorker<Boolean, Object>
	{
		PreviewProxy proxy_;
		int length_;

		public TextLoader(PreviewProxy proxy, int length)
		{
			proxy_ = proxy;
			length_ = length;
		}

		@Override
		protected Boolean doInBackground() throws Exception
		{
			String text = null;
			boolean ok = false;
			try
			{
				try (InputStream fis = Files.newInputStream(IOTool.getPath(proxy_.uri_)))
				{
					char[] buffer = new char[length_];
					InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
					final int l = isr.read(buffer);
					if (l > 0)
					{
						text = new String(buffer, 0, l);
						ok = true;
					}
				}
			}
			catch (Exception e)
			{
			}
			synchronized (proxy_)
			{
				proxy_.imageContent_ = null;
				if (ok)
				{
					proxy_.textContent_ = text;
					proxy_.message_ = null;
				}
				else
				{
					proxy_.textContent_ = null;
					proxy_.message_ = config_.errorText_;
				}
				proxy_.complete = true;
			}

			return ok;
		}

		@Override
		protected void done()
		{
			if (proxy_ != null && proxy_.activeAndPending)
			{
				proxy_.activeAndPending = false;
				if (config_ != null)
					config_.update(proxy_);
			}
		}
	}

	@Override
	protected PreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		final String fname = file.getFileName()
								 .toString();

		final PreviewProxy proxy = new PreviewProxy();
		proxy.uri_ = file.normalize()
						 .toUri()
						 .toString();
		proxy.config_ = config_;

		new TextLoader(proxy, previewTextLength_).execute();
		return proxy;
	}
}
