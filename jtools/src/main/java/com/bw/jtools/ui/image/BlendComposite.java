package com.bw.jtools.ui.image;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Composite to mask the destination by gray- and alpha of the source.<br>
 * This creates a "Mask" effect on the destination.
 */
public class BlendComposite implements Composite
{
	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints)
	{
		return new BlendCompositeContext(srcColorModel, dstColorModel, mode_);
	}

	public enum Mode {
		/** mask the destination by the gray- and alpha-value of the source. */
		MASK_GRAY,
		/** Overlays source on destination. */
		OVERLAY,
		/*** Multiply */
		MULTIPLY
	}


	public BlendComposite(Mode mode)
	{
		mode_ = mode;
	}

	private final Mode mode_;

	static class BlendCompositeContext implements CompositeContext
	{

		private final ColorModel srcCM_;
		private final ColorModel dstCM_;
		private final Mode mode_;

		final static int PRECBITS = 22;

		BlendCompositeContext(final ColorModel srcCM, final ColorModel dstCM, Mode mode)
		{
			this.srcCM_ = srcCM;
			this.dstCM_ = dstCM;
			this.mode_ = mode;
		}

		public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut)
		{
			final int w = Math.min(src.getWidth(), dstIn.getWidth());
			final int h = Math.min(src.getHeight(), dstIn.getHeight());

			// Reuse data to reduce memory fragmentation.
			Object srcPixel = null;
			Object dstPixel = null;
			Object data = null;

			int a1,r1,g1,b1;
			int a2,r2,g2,b2;
			int rgb1, rgb2;
			int gray;

			for (int y = 0; y < h; ++y)
			{
				for (int x = 0; x < w; ++x)
				{
					rgb1 = srcCM_.getRGB(srcPixel = src.getDataElements(x, y, srcPixel));
					rgb2 = dstCM_.getRGB(dstPixel = dstIn.getDataElements(x, y, dstPixel));

					a1 = rgb1 >>> 24;
					r1 = (rgb1 >> 16) & 0xFF;
					g1 = (rgb1 >> 8) & 0xFF;
					b1 = (rgb1) & 0xFF;

					a2 = (rgb2 >>> 24);
					r2 = (rgb2 >> 16) & 0xFF;
					g2 = (rgb2 >> 8) & 0xFF;
					b2 = (rgb2) & 0xFF;

					switch (mode_) {
						case MASK_GRAY:
							gray = (int) (0.2989 * r1 + 0.5870 * g1 + 0.1140 * b1);
							a2 = gray > 255 ? 0 : (((a1 * (255 - gray)) / 255) * a2) / 255;
							break;
						case OVERLAY:
							r2 = r2 < 128 ? r2 * r1 >> 7 : 255 - ((255 - r2) * (255 - r1) >> 7);
							g2 = g2 < 128 ? g2 * g1 >> 7 : 255 - ((255 - g2) * (255 - g2) >> 7);
							b2 = b2 < 128 ? b2 * b1 >> 7 : 255 - ((255 - b2) * (255 - b1) >> 7);
							a2 = a1 + a2 - (a1 * a2) / 255;
							if ( a2 > 255 ) a2 = 255;
							break;
						case MULTIPLY:
							r2 = (r1 * r2) >> 8;
							g2 = (g1 * g2) >> 8;
							b2 = (b1 * b2) >> 8;
							a2 = a1 + a2 - (a1 * a2) / 255;
							if ( a2 > 255 ) a2 = 255;
							break;
					}
					rgb2 = (a2 << 24) | (r2 << 16) | (g2 << 8) | b2;
					dstOut.setDataElements(x, y, data = dstCM_.getDataElements(rgb2, data));
				}
			}
		}

		@Override
		public void dispose()
		{
			// nothing for this implementation
		}
	}


}
