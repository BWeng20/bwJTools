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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Holds and paints a list of shapes.
 */
public final class ShapePainter
{
	private Rectangle2D.Double area_ = null;
	private final List<AbstractShape> shapes_ = new ArrayList<>();
	private final static BasicStroke defaultStroke_ = new BasicStroke(1f);
	private double scaleX_ = 1.0f;
	private double scaleY_ = 1.0f;
	private boolean adaptOffset_ = true;

	private boolean measureTime_ = false;
	private long lastMSNeeded_ = 0;

	private double offsetX_ = 0;
	private double offsetY_ = 0;

	/**
	 * Returns the covered area according to shapes and scale.
	 */
	public Rectangle2D.Double getArea()
	{
		if (area_ == null)
			return new Rectangle2D.Double(0, 0, 0, 0);
		else if (adaptOffset_)
			return new Rectangle2D.Double(0, 0, scaleX_ * area_.width, scaleY_ * area_.height);
		else
			return new Rectangle2D.Double(scaleX_ * area_.x, scaleY_ * area_.y, scaleX_ * area_.width, scaleY_ * area_.height);
	}

	/**
	 * Auto adapt origin. Manual offset is ignored.
	 * Upper-left corner is moved to 0,0.
	 */
	public void setAdaptOrigin( boolean on )
	{
		adaptOffset_ = on;
	}

	/**
	 * Sets manual offset.
	 */
	public void setOrigin( double x, double y )
	{
		offsetX_ = x;
		offsetY_ = y;
	}

	/**
	 * Gets the absolute width of the covered area.
	 */
	public double getAreaWidth()
	{
		return area_ == null ? 0 : scaleX_ * (adaptOffset_ ? area_.width : (area_.x + area_.width) );
	}

	/**
	 * Gets the absolute height of the covered area.
	 */
	public double getAreaHeight()
	{
		return area_ == null ? 0 : scaleY_ * (adaptOffset_ ? area_.height : (area_.y + area_.height));
	}

	public void clearShapes()
	{
		shapes_.clear();
		area_ = null;
	}

	/**
	 * Adds a shape.
	 */
	public void addShape(AbstractShape shape)
	{
		shapes_.add(shape);

		Rectangle2D transRect = shape.getTransformedBounds();
		if (area_ == null)
			area_ = new Rectangle2D.Double(transRect.getX(), transRect.getY(), transRect.getWidth(), transRect.getHeight());
		else
			area_ = (Rectangle2D.Double) area_.createUnion(transRect);
	}

	/**
	 * Adds a number of shapes.
	 * Iterates across the collection and calls {@link #addShape(AbstractShape)}.
	 */
	public void addShapes(Collection<AbstractShape> shapes)
	{
		if (shapes != null)
			for (AbstractShape s : shapes)
				addShape(s);
	}

	/**
	 * Sets X- and Y-Scale factor.
	 */
	public void setScale(double scaleX, double scaleY)
	{
		scaleX_ = scaleX;
		scaleY_ = scaleY;
	}

	/**
	 * Gets X-Scale factor.
	 */
	public double getXScale()
	{
		return scaleX_;
	}

	/**
	 * Gets Y-Scale factor.
	 */
	public double getYScale()
	{
		return scaleY_;
	}

	/**
	 * Paints the shapes.
	 * @param ctx Graphic context, will NOT be restored.
	 *
	 * @param clearArea If true the area of the shapes is cleared with the current color.
	 */
	public void paintShapes(Context ctx, boolean clearArea)
	{
		if (area_ == null)
			return;

		final long ms = (measureTime_) ? System.currentTimeMillis() : 0;

		Context lct = new Context(ctx, false);
		final Graphics2D g2D = lct.g2D_;

		g2D.scale(scaleX_, scaleY_);
		if (adaptOffset_)
			g2D.translate(-area_.x, -area_.y);
		else
			g2D.translate(offsetX_, offsetY_);

		if (clearArea)
			g2D.fill(area_);

		lct.aft_ = lct.g2D_.getTransform();
		for (AbstractShape shape : shapes_)
			shape.paint(lct);

		if (measureTime_)
			lastMSNeeded_ = System.currentTimeMillis() - ms;
	}

	public void setTimeMeasurementEnabled(boolean measureTime)
	{
		this.measureTime_ = measureTime;
	}

	public long getMeasuredTimeMS()
	{
		return lastMSNeeded_;
	}

}
