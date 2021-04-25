package com.bw.jtools.ui.image;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Composite to mask the destination by gray- and alpha of the source.<br>
 * This creates a "Mask" effect on the destination.
 */
public class MaskComposite implements Composite
{
	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints)
	{
		return new MaskCompositeContext(srcColorModel, dstColorModel);
	}

	static class MaskCompositeContext implements CompositeContext
	{

		private final ColorModel srcCM;
		private final ColorModel dstCM;

		final static int PRECBITS = 22;
		final static int WEIGHT_R = (int) ((1 << PRECBITS) * 0.299);
		final static int WEIGHT_G = (int) ((1 << PRECBITS) * 0.578);
		final static int WEIGHT_B = (int) ((1 << PRECBITS) * 0.114);
		final static int SRCALPHA = (int) ((1 << PRECBITS) * 0.667);

		MaskCompositeContext(final ColorModel srcCM, final ColorModel dstCM)
		{
			this.srcCM = srcCM;
			this.dstCM = dstCM;
		}

		public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut)
		{
			final int w = Math.min(src.getWidth(), dstIn.getWidth());
			final int h = Math.min(src.getHeight(), dstIn.getHeight());
			for (int y = 0; y < h; ++y)
			{
				for (int x = 0; x < w; ++x)
				{

					int rgb1 = srcCM.getRGB(src.getDataElements(x, y, null));
					int rgb2 = dstCM.getRGB(dstIn.getDataElements(x, y, null));

					int a1 = rgb1 >>> 24;
					int r1 = (rgb1 >> 16) & 0xFF;
					int g1 = (rgb1 >> 8) & 0xFF;
					int b1 = (rgb1) & 0xFF;

					int gray = (int) (0.2989 * r1 + 0.5870 * g1 + 0.1140 * b1);
					if (gray > 255) gray = 255;
					gray = (a1 * (255 - gray)) / 255;

					int a2 = (gray * (rgb2 >>> 24)) / 255;
					int r2 = (rgb2 >> 16) & 0xFF;
					int g2 = (rgb2 >> 8) & 0xFF;
					int b2 = (rgb2) & 0xFF;

					rgb2 = (a2 << 24) | (r2 << 16) | (g2 << 8) | b2;
					dstOut.setDataElements(x, y, dstCM.getDataElements(rgb2, null));
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
