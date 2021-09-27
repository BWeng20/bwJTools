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
import com.bw.jtools.shape.ShapeIcon;
import com.bw.jtools.svg.SVGConverter;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles preview of supported image files.
 */
public class SVGPreviewHandler extends PreviewHandler
{
	protected static final String cachePrefix_ = "svg";
	protected long maxFileLength_ = 1024 * 1024 * 10;

	/**
	 * Label to show images and alternative messages.
	 */
	protected JLabel view_;


	/**
	 * Creates a new Image Preview handler.
	 */
	public SVGPreviewHandler()
	{
		// File name pattern for supported files.
		super("(?i).*\\.svg");

	}

	@Override
	public Component getPreviewComponent(PreviewProxy proxy)
	{
		if ( view_ == null )
		{
			view_ = new JLabel();
			view_.setVerticalAlignment(JLabel.CENTER);
			view_.setHorizontalAlignment(JLabel.CENTER);
		}

		SVGPreviewProxy svgProxy = (SVGPreviewProxy)proxy;

		if (svgProxy != null && svgProxy.complete)
		{
			view_.setIcon(new ShapeIcon(svgProxy.shapes_) );
		}
		else
		{
			view_.setIcon(null);
		}

		return view_;
	}

	@Override
	protected String getCacheKey(Path file, String canonicalPath)
	{
		return cachePrefix_ + canonicalPath;
	}

	/**
	 * Reloads the Image Proxy.
	 */
	@Override
	protected void updatePreviewProxy(PreviewProxy proxy)
	{
		load((SVGPreviewProxy)proxy);
	}

	private void load(SVGPreviewProxy proxy)
	{
		proxy.complete = false;
		new SVGLoader(proxy).execute();
	}

	/**
	 * Swing worker to load the svg content.
	 */
	protected class SVGLoader extends SwingWorker<Boolean, Object>
	{
		SVGPreviewProxy proxy_;

		public SVGLoader(SVGPreviewProxy proxy)
		{
			proxy_ = proxy;
		}

		@Override
		protected Boolean doInBackground() throws Exception
		{
			Path file;
			SVGConverter svg = null;
			synchronized (proxy_)
			{
				file = proxy_.file_;
			}
			try
			{
				svg = new SVGConverter(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
			}
			catch (Exception e)
			{
				Log.error("Failed to load "+file, e);
			}
			synchronized (proxy_)
			{
				if (svg != null)
				{
					proxy_.shapes_ = svg.getShapes();
					proxy_.message_ = null;
				}
				else
				{
					proxy_.shapes_.clear();
					proxy_.message_ = config_.errorText_;
				}
				proxy_.complete = true;
			}
			return svg != null;
		}

		@Override
		protected void done()
		{
			if (proxy_ != null && proxy_.activeAndPending)
			{
				proxy_.activeAndPending = false;
				if (config_ != null)
					config_.update(proxy_, SVGPreviewHandler.this);
			}
		}
	}

	/**
	 * Creates a new Image Proxy that triggers and monitors the load process.
	 */
	@Override
	protected SVGPreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		final SVGPreviewProxy proxy = new SVGPreviewProxy(this);
		proxy.file_ = file;
		proxy.config_ = config_;
		load( proxy	);
		return proxy;
	}

}
