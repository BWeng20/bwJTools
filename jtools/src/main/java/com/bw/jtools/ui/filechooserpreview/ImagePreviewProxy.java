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
 * Helper class to track the loading state of a preview-image.<br>
 */
class ImagePreviewProxy extends PreviewProxy implements ImageObserver
{
	/**
	 * The original image.
	 */
	protected Image image_;

	/**
	 * The original width of the image
	 */
	protected int width_ = -1;

	/**
	 * The original height of the image
	 */
	protected int height_ = -1;

	protected final ImagePreviewHandler handler_;

	/**
	 * Image content.
	 */
	public Image imageContent_;

	public ImagePreviewProxy(ImagePreviewHandler imagePreviewHandler)
	{
		super();
		handler_ = imagePreviewHandler;
	}


	/**
	 * Is called if more of the image gets available.
	 * Used for the scaled and original image.
	 */
	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
	{
		if (img == image_)
		{
			boolean dimReady = height_ != -1 && width_ != -1;
			if (!dimReady)
			{
				if (0 != (ImageObserver.HEIGHT & infoflags))
					height_ = height;
				if (0 != (ImageObserver.WIDTH & infoflags))
					width_ = width;

				dimReady = height_ != -1 && width_ != -1;
				if (dimReady)
				{
					InfoEntry imageInfo = new InfoEntry();
					imageInfo.name = I18N.getText("filechooser.preview.dimension");
					imageInfo.value = String.valueOf(width_) + " x " + String.valueOf(height_);
					additionalInformation_.add(imageInfo);
				}
			}
			if (Log.isDebugEnabled())
				log("Original flags " + infoflags + ": " + width_ + "x" + height_);
			return (ImageObserver.ALLBITS > infoflags) && !dimReady;
		}
		else if (ImageObserver.ALLBITS <= infoflags)
		{
			synchronized (this)
			{
				complete = true;
				if (activeAndPending)
				{
					log("Finished (flags " + infoflags + ")");
					activeAndPending = false;
					if (ImageObserver.ALLBITS == infoflags)
					{
						message_ = null;
					}
					else
					{
						image_ = null;
						imageContent_ = config_.errorImage_;
						message_ = config_.errorText_;
					}
					config_.update(this, handler_);
				}
			}
			// Stop observing.
			return false;
		}
		else
			return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString())
		  .append(" image:");
		if (imageContent_ == null)
			sb.append("none");
		else
		{
			sb.append(imageContent_.getClass()
								   .getSimpleName())
			  .append(' ');
			sb.append(imageContent_.getWidth(null))
			  .append('x')
			  .append(imageContent_.getHeight(null));
		}
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
		imageContent_ = null;
		image_ = null;
	}

}
