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
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles preview of text files.
 */
public class TextPreviewHandler extends PreviewHandler
{

	/**
	 * TextArea to show text based content previews.
	 */
	protected TextPreview previewText_;

	/**
	 * Creates a new handler.
	 */
	protected TextPreviewHandler()
	{
		// File name pattern for supported text-files.
		super("(?i).*\\.(txt|text|xml|html|htm|js|cmd|sh|sql|java)");
	}

	/**
	 * Maximum length of text to load for preview.
	 */
	protected int previewTextLength_ = 5*1024;

	/**
	 * Maximum lines of text to load for preview.
	 */
	protected int previewLines_ = 20;

	/**
	 * Swing worker to load the text.
	 */
	protected class TextLoader extends SwingWorker<Boolean, Object>
	{
		TextPreviewProxy proxy_;

		public TextLoader(TextPreviewProxy proxy)
		{
			proxy_ = proxy;
		}

		@Override
		protected Boolean doInBackground() throws Exception
		{
			int lines;
			int maxlength;
			long lastReadPos;
			Path file;
			synchronized (proxy_)
			{
				lines = proxy_.textMaxLines_;
				maxlength = proxy_.textMaxLength_;
				proxy_.orgTextMaxLength_ = proxy_.textMaxLength_;
				proxy_.orgTextMaxLines_=  proxy_.textMaxLines_;
				lastReadPos = proxy_.lastReadPos_;
				file = proxy_.file_;
			}
			if ( maxlength < 1024 ) maxlength = 1024;
			if ( lines < 5 ) lines = 5;
			StringBuilder text = new StringBuilder(maxlength);
			boolean ok = false;
			try
			{
				try (InputStream fis = Files.newInputStream(IOTool.getPath(file.toUri().toString())))
				{
					BufferedReader reader = new BufferedReader( new InputStreamReader(fis,StandardCharsets.UTF_8), 2048 );

					if ( lastReadPos > 0 )
						reader.skip(lastReadPos);

					int lineCounter = 0;
					int c;
					while ((c = reader.read()) >= 0 && lineCounter<lines && text.length() < maxlength)
					{
						++lastReadPos;
						if ((c == '\n') || (c == '\r'))
						{
							text.append('\n');
							++lineCounter;
						} else
						{
							text.append((char)c);
						}
					}
					ok = true;
				}
			}
			catch (Exception e)
			{
			}
			synchronized (proxy_)
			{
				if (ok)
				{
					proxy_.textContent_ = text.toString();
					proxy_.message_ = null;
				}
				else
				{
					proxy_.textContent_ = null;
					proxy_.message_ = config_.errorText_;
				}
				proxy_.lastReadPos_ = lastReadPos;
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
					config_.update(proxy_, TextPreviewHandler.this);
			}
		}
	}

	@Override
	protected void updatePreviewProxy(PreviewProxy proxy)
	{
		proxy.complete = false;
		new TextLoader((TextPreviewProxy)proxy).execute();
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
			TextPreviewProxy txtProxy = (TextPreviewProxy) proxy;
			if (txtProxy.complete)
				previewText_.setText(txtProxy.textContent_);
			else
				// MessageText....
				previewText_.setText("");
		}

		return previewText_;
	}

	@Override
	protected PreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		final String fname = file.getFileName()
								 .toString();

		final TextPreviewProxy proxy = new TextPreviewProxy(this);
		proxy.file_ = file;
		proxy.config_ = config_;

		new TextLoader(proxy).execute();
		return proxy;
	}
}
