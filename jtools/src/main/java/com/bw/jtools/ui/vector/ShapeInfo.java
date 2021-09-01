package com.bw.jtools.ui.vector;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public final class ShapeInfo
{
	public String id_;
	public Shape shape_;
	public Stroke stroke_;
	public Paint paint_;
	public float opacity_;
	public Paint fill_;
	public float fillOpacity_;
	public AffineTransform aft_;

	public ShapeInfo(Shape shape, Stroke stroke, Paint paint, float opacity, Paint fill, float fillOpacity)
	{
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;
		this.opacity_ = opacity;
		this.fillOpacity_ = fillOpacity;
	}

}
