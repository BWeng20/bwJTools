/*
 * (c) copyright 2022 Bernd Wengenroth
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
package com.bw.jtools.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Transferable implementation to copy an image to clipboard.
 * <br>
 * Remind that Java currently doesn't support transparency for
 * clipboard operations.<br>
 * If you source has an alpha channel, it is wise to repaint
 * the image with opaque background, otherwise the background will get black.<br>
 * For this case you can use {@link #TransferableImage(java.awt.image.BufferedImage, java.awt.Color)}
 */
public final class TransferableImage implements Transferable
{
	final Image image_;

	/**
	 * Creates a transferable with the given image.
	 *
	 * @param image The image to copy.
	 */
	public TransferableImage(Image image)
	{
		image_ = image;
	}

	/**
	 * Creates a transferable with the given image, but redraws the image
	 * with opaque background if it has an alpha-channel.
	 *
	 * @param toClipboard       The image to copy.
	 * @param backgroundIfAlpha The background-color for redraw. Can be null, in this case WHITE is used.
	 */
	public TransferableImage(BufferedImage toClipboard, Color backgroundIfAlpha)
	{
		if (toClipboard != null)
		{
			if (backgroundIfAlpha == null) backgroundIfAlpha = Color.WHITE;
			// Java doesn't support transparency in clipboard operations.
			// To avoid a black background, repaint with white.
			if (toClipboard.getColorModel()
						   .hasAlpha())
			{
				BufferedImage copy = new BufferedImage(toClipboard.getWidth(), toClipboard.getHeight(), BufferedImage.TYPE_INT_RGB);
				copy.createGraphics()
					.drawImage(toClipboard, 0, 0, backgroundIfAlpha, null);
				image_ = copy;
			}
			else
			{
				image_ = toClipboard;
			}
		}
		else
			image_ = null;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(DataFlavor.imageFlavor) && image_ != null)
			return image_;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		DataFlavor[] flavors = new DataFlavor[1];
		flavors[0] = DataFlavor.imageFlavor;
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor == DataFlavor.imageFlavor;
	}
}
