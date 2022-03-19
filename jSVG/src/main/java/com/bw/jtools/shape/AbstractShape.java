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

package com.bw.jtools.shape;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * A abstract base for shapes.
 */
public abstract class AbstractShape
{
	/**
	 * Placeholder for "currentColor". The color from caller-perspective.
	 */
	public static final Color CURRENT_COLOR = new Color(0,0,0);

	/**
	 * Placeholder for "background", an internal extension to access the background of the painting component.
	 */
	public static final Color CURRENT_BACKGROUND = new Color( 0xce, 0xce,0xce);

	/**
	 * Placeholder for "none" color.
	 */
	public static final Color NONE = new Color(0,0,0,0);

	/**
	 * Id to identify the shape group in the some document.
	 */
	public final String id_;

	/**
	 * Constructor to initialize,
	 */
	protected AbstractShape(String id)
	{
		this.id_ = id;
	}

	public abstract void paint( Context ctx );

	/**
	 * Get bounds of the transformed shape including stroke-width.
	 */
	public abstract Rectangle2D getTransformedBounds();

	/**
	 * Translates special paints to values.
	 */
	protected Paint transatePaint(Context ctx, Paint p )
	{
		if (p == null)
			return Color.BLACK;
		else if (p == NONE)
			return null;
		else if (p == CURRENT_COLOR)
			return ctx.currentColor_;
		else if (p == CURRENT_BACKGROUND)
			return ctx.currentBackground_;
		else
			return p;
	}

}
