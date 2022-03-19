/*
 * (c) copyright 2021 Bernd Wengenroth
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

package com.bw.jtools.shape.filter;

import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PainterBuffers
{
	private Map<String, BufferedImage> buffers_ = new HashMap<>();
	private Map<String, BufferedImage> tempBuffers_ = new HashMap<>();
	private GraphicsConfiguration cfg_;
	public static long buffersCreated_;

	public PainterBuffers()
	{
	}

	public void setConfiguration( GraphicsConfiguration cfg )
	{
		cfg_ = cfg;
	}

	public void clear()
	{
		clearBuffers(buffers_);
		clearBuffers(tempBuffers_);
	}

	private static <T> void clearBuffers( Map<T,BufferedImage> buffers )
	{
		for ( BufferedImage i : buffers.values())
			i.flush();
		buffers.clear();
	}

	public void addBuffer(String name, BufferedImage img)
	{
		buffers_.put( name, img );
	}

	public BufferedImage getSourceBuffer(String name)
	{
		return buffers_.get( name );
	}

	/**
	 * Get the source buffer as alpha (rgba with rgb part black).
	 * @param name Name of buffer (without "alpha" in name).
	 */
	public BufferedImage getSourceAlphaBuffer(String name)
	{
		final String alphaName = name+"Alpha".intern();
		BufferedImage i =  buffers_.get( alphaName );
		if ( i == null)
		{
			i = getSourceBuffer(name);
			if ( i != null )
			{
				Raster in = i.getData();
				++buffersCreated_;
				BufferedImage alpha = cfg_.createCompatibleImage(i.getWidth(), i.getHeight(), i.getTransparency());

				int N = in.getNumBands();
				final float[] scaleFactors = new float[N];
				final float[] offsets = new float[N];
				Arrays.fill(scaleFactors, 0);
				scaleFactors[N-1] = 1f;
				Arrays.fill(offsets, 0);

				RescaleOp extractAlpha = new RescaleOp(scaleFactors, offsets, null);
				extractAlpha.filter( in, alpha.getRaster() );

				buffers_.put( alphaName, alpha);
				i = alpha;
			}
		}
		return i;
	}

	/**
	 * Get a temporary buffer.
	 * @param idx The index of the buffer. Each index gets it own buffer.
	 */
	public BufferedImage getTemporaryBuffer(int idx, double width, double height)
	{
		String key = idx+":"
				+((int)Math.ceil(width/100)*100)+":"
				+((int)Math.ceil(height/100)*100);
		return getBuffer( key, tempBuffers_, width, height );
	}

	public BufferedImage getTargetBuffer(String name, double width, double height)
	{
		return getBuffer( name, buffers_, width, height );
	}

	private <T> BufferedImage getBuffer(
			T key, Map<T, BufferedImage> buffer, double width, double height)
	{
		BufferedImage i = buffer.get(key);
		if ( i == null || i.getWidth()<width || i.getHeight()<height )
		{
			if (width<=0.5) width = 1;
			if (height<=0.5) height = 1;
			++buffersCreated_;
			i = cfg_.createCompatibleImage((int)width,(int)height, Transparency.TRANSLUCENT);
			buffer.put( key, i );
			System.out.println("Buffer Created "+key+" "+width+"x"+height+" -> "+i.getWidth()+"x"+i.getHeight());
		}


		return i;
	}

}
