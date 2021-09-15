package com.bw.jtools.ui.vector;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class ShapePainter
{
	private Rectangle2D.Double area_ = null;
	private final List<ShapeInfo> shapes_ = new ArrayList<>();
	private final static BasicStroke defaultStroke_ = new BasicStroke(1f);
	private double scaleX_ = 1.0f;
	private double scaleY_ = 1.0f;
	private boolean keepOffset_ = false;

	/**
	 * Returnes the covered area according to shapes and scale.
	 *
	 * @return
	 */
	public Rectangle2D.Double getArea()
	{
		if (keepOffset_)
			return new Rectangle2D.Double(scaleX_ * area_.x, scaleY_ * area_.y, scaleX_ * area_.width, scaleY_ * area_.height);
		else
			return new Rectangle2D.Double(0, 0, scaleX_ * area_.width, scaleY_ * area_.height);
	}

	/**
	 * Gets the absolute width of the covered area.
	 */
	public double getAreaWidth()
	{
		return area_ == null ? 0 : scaleX_ * (keepOffset_ ? (area_.x + area_.width) : area_.width);
	}

	/**
	 * Gets the absolute height of the covered area.
	 */
	public double getAreaHeight()
	{
		return area_ == null ? 0 : scaleY_ * (keepOffset_ ? (area_.x + area_.height) : area_.height);
	}

	/**
	 * Adds a shape.
	 */
	public void addShape(ShapeInfo shape)
	{
		shapes_.add(shape);

		Rectangle2D r = shape.shape_.getBounds2D();

		final float lw = ((shape.stroke_ instanceof BasicStroke) ? (BasicStroke) shape.stroke_ : defaultStroke_).getLineWidth();
		Rectangle2D transRect = shape.aft_.createTransformedShape(new Rectangle2D.Double(r.getX() - lw, r.getY() - lw, r.getWidth() + 2 * lw, r.getHeight() + 2 * lw))
										  .getBounds2D();
		if (area_ == null)
			area_ = new Rectangle2D.Double(transRect.getX(), transRect.getY(), transRect.getWidth(), transRect.getHeight());
		else
			area_ = (Rectangle2D.Double) area_.createUnion(transRect);
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
	 *
	 * @param clearArea If true the area of the shapes is cleared with the current color.
	 */
	public void paintShapes(Graphics2D g2D, boolean clearArea)
	{
		g2D.scale(scaleX_, scaleY_);
		if (!keepOffset_)
			g2D.translate(-area_.x, -area_.y);
		if (clearArea)
			g2D.fill(area_);

		float opacity = 1f;
		AffineTransform orgAft = g2D.getTransform();
		AffineTransform aft = new AffineTransform();

		for (ShapeInfo shape : shapes_)
		{
			aft.setTransform(orgAft);
			aft.concatenate(shape.aft_);
			g2D.setTransform(aft);

			if (shape.fill_ != null)
			{
				if (shape.fillOpacity_ != opacity)
				{
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, shape.fillOpacity_));
					opacity = shape.fillOpacity_;
				}
				g2D.setPaint(shape.fill_);
				g2D.fill(shape.shape_);
			}
			if (shape.paint_ == null)
			{
				if (shape.fill_ == null)
				{
					if (1f != opacity)
					{
						g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
						opacity = 1f;
					}
					g2D.setStroke(defaultStroke_);
					g2D.draw(shape.shape_);
				}
			}
			else
			{
				if (shape.strokeOpacity_ != opacity)
				{
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, shape.strokeOpacity_));
					opacity = shape.strokeOpacity_;
				}
				g2D.setPaint(shape.paint_);
				g2D.setStroke(shape.stroke_);
				g2D.draw(shape.shape_);
			}
		}
	}


}
