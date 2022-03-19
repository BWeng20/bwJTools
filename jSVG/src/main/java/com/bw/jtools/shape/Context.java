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

package com.bw.jtools.shape;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Context
{
	public Graphics2D g2D_;
	public AffineTransform aft_;
	public Paint currentColor_;
	public Paint currentBackground_;
	public boolean debug_ = false;
	private final boolean newContext_;

	public Context(Context ctx)
	{
		this( ctx, true );
	}

	public Context(Context ctx, boolean createNewContext)
	{
		this.newContext_ = createNewContext;
		this.g2D_ = createNewContext ? (Graphics2D)ctx.g2D_.create() : ctx.g2D_;
		this.aft_ = ctx.aft_;
		this.currentColor_ = ctx.currentColor_;
		this.currentBackground_ = ctx.currentBackground_;
		this.debug_ = ctx.debug_;
	}

	public Context(Graphics g2D)
	{
		this( g2D, true );
	}

	public Context(Graphics g2D, boolean createNewContext)
	{
		this.newContext_ = createNewContext;
		this.g2D_ = (Graphics2D)(createNewContext ? g2D.create() : g2D);
		this.aft_ = this.g2D_.getTransform();
		this.currentColor_ = this.g2D_.getPaint();
	}

	public Context(BufferedImage source, Context ctx )
	{
		this.newContext_ = true;
		this.g2D_ = source.createGraphics();
		this.aft_ = ctx.aft_;
		this.currentColor_ = ctx.currentColor_;
		this.currentBackground_ = ctx.currentBackground_;
		this.debug_ = ctx.debug_;
	}

	public void dispose()
	{
		if ( newContext_ )
		{
			g2D_.dispose();
			g2D_ = null;
		}
	}
}
