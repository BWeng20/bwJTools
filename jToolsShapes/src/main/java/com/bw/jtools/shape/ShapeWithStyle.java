package com.bw.jtools.shape;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * A shape plus additional style information.
 */
public final class ShapeWithStyle
{
	/** Id to identify the shape in the some document. */
	public String id_;

	/** The shape. */
	public Shape shape_;

	/** The stroke to render the outline. */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.</br>
	 * Can be null.
	 */
	public Paint paint_;

	/** Overall Stroke opacity. Is equal to stroke-opacity * opacity */
	public float strokeOpacity_;

	/**
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public Paint fill_;

	/**
	 * Final fill opacity. Is equal to fill-opacity * opacity
	 */
	public float fillOpacity_;

	/**
	 * Transform to be applied to the graphics context.</br>
	 * Never null.
	 */
	public AffineTransform aft_;

	public Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public ShapeWithStyle(Shape shape, Stroke stroke, Paint paint,
						  float strokeOpacity, Paint fill, float fillOpacity, Shape clipping)
	{
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;
		this.strokeOpacity_ = strokeOpacity;
		this.fillOpacity_ = fillOpacity;
		this.clipping_ = clipping;
	}

}
