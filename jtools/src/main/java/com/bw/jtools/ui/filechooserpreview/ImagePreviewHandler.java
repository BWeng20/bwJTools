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

import javax.swing.*;
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
	 * Label to show images and alternative messages.
	 */
	protected ImagePreview imageView_;


	/**
	 * Creates a new Image Preview handler.
	 */
	public ImagePreviewHandler()
	{
		// File name pattern for supported images.
		super("(?i).*\\.(jpg|gif|png|jpeg|bmp)");

	}

	@Override
	public Component getPreviewComponent(PreviewProxy proxy)
	{
		if ( imageView_ == null )
		{
			imageView_ = new ImagePreview();
			if ( config_ != null )
				imageView_.setPreferredSize(new Dimension(config_.previewWidth_, config_.previewWidth_));
		}

		ImagePreviewProxy imageProxy = (ImagePreviewProxy)proxy;

		if (imageProxy != null && imageProxy.complete)
		{
			imageView_.setImage(imageProxy.imageContent_);
		}
		else
		{
			imageView_.setImage(null);
		}

		return imageView_;
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
		load((ImagePreviewProxy) proxy);
	}

	private void load( ImagePreviewProxy proxy )
	{
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		try
		{
			proxy.image_ = toolkit.createImage(proxy.file_.toUri().toURL());
		}
		catch (Exception e)
		{
		}
		if (proxy.image_ == null)
		{
			proxy.complete = true;
			proxy.image_ = config_.errorImage_;
			proxy.message_ = config_.errorText_;
			Log.error("Failed " + proxy.name_);
		}
		else
		{
			proxy.imageContent_ = proxy.image_.getScaledInstance(config_.previewWidth_, -1, Image.SCALE_SMOOTH);
			proxy.complete = toolkit.prepareImage(proxy.imageContent_, -1, -1, proxy);
			proxy.width_ = proxy.image_.getWidth(proxy);
			proxy.height_ = proxy.image_.getHeight(proxy);
			proxy.message_ = config_.loadingText_;
		}
	}

	/**
	 * Creates a new Image Proxy that triggers and monitors the load process.
	 */
	@Override
	protected ImagePreviewProxy createPreviewProxy(Path file, String canonicalPath)
	{
		final ImagePreviewProxy proxy = new ImagePreviewProxy(this);
		proxy.file_ = file;
		proxy.config_ = config_;
		load( proxy	);
		return proxy;
	}

	@Override
	protected void setConfiguration(PreviewConfig config)
	{
		super.setConfiguration(config);
		if (config_ != null)
		{
			cachePrefix_ = String.valueOf(config_.previewWidth_) + ":";
			if ( imageView_ != null )
				imageView_.setPreferredSize(new Dimension(config_.previewWidth_,config_.previewWidth_));
		}
	}

}
