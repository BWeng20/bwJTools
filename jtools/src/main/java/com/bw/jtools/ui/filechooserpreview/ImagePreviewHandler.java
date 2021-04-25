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

import java.awt.*;
import java.nio.file.Path;

/**
 * Handles preview of supported image files.
 */
public class ImagePreviewHandler extends PreviewHandler
{
	protected String cachePrefix_;
	protected long maxFileLength_ = 1024 * 1024 * 10;

	/**
	 * Creates a new Image Preview handler.
	 */
	public ImagePreviewHandler()
	{
		// File name pattern for supported images.
		super("(?i).*\\.(jpg|gif|png|jpeg|bmp)");
	}

	@Override
	protected String getCacheKey(Path file, String canonicalPath)
	{
		return cachePrefix_ + canonicalPath;
	}

	/**
	 * Creates a new Image Proxy that triggers and monitors the load process.
	 */
	@Override
	protected PreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		final ImagePreviewProxy proxy = new ImagePreviewProxy();
		proxy.config_ = config_;

		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		try
		{
			proxy.image = toolkit.createImage(file.toUri()
												  .toURL());
		}
		catch (Exception e)
		{
		}
		if (proxy.image == null)
		{
			proxy.complete = true;
			proxy.image = config_.errorImage_;
			proxy.message_ = config_.errorText_;
			Log.error("Failed " + file.getFileName());
		}
		else
		{
			proxy.imageContent_ = proxy.image.getScaledInstance(config_.previewWidth_, -1, Image.SCALE_SMOOTH);
			proxy.complete = toolkit.prepareImage(proxy.imageContent_, -1, -1, proxy);
			proxy.width_ = proxy.image.getWidth(proxy);
			proxy.height_ = proxy.image.getHeight(proxy);
			proxy.message_ = config_.loadingText_;
		}
		return proxy;
	}

	@Override
	protected void setConfiguration(PreviewConfig config)
	{
		super.setConfiguration(config);
		if (config_ != null)
			cachePrefix_ = String.valueOf(config_.previewWidth_) + ":";
	}

}
