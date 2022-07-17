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

import java.awt.Color;
import java.awt.image.BufferedImage;

public class SpecularLighting extends FilterBaseSingleSource
{

	public double surfaceScale_;
	public double specularConstant_;
	public double specularExponent_;
	public Double dx_;
	public Double dy_;

	public Color color_;

	public LightSource light_;

	@Override
	protected void render(PainterBuffers buffers, String targetName, BufferedImage src, BufferedImage target, double scaleX, double scaleY)
	{

		// @TODO: implement this crazy filter. Ask mathematically gifted relatives for this!
		src.copyData(target.getRaster());
	}

	public SpecularLighting(String source, String target,
							double surfaceScale,
							double specularConstant,
							double specularExponent,
							Double dx, Double dy,
							LightSource light)
	{
		super(source, target);

		surfaceScale_ = surfaceScale;
		specularConstant_ = specularConstant;
		specularExponent_ = specularExponent;
		dx_ = dx;
		dy_ = dy;
		light_ = light;
	}



}



