package com.bw.jtools.shape;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * A shape plus additional style information.
 */
public final class ShapeWithStyle
{
	public static final Color NONE = new Color(0,0,0,0);

	/**
	 * Id to identify the shape in the some document.
	 */
	public final String id_;

	/**
	 * The shape.
	 */
	public final Shape shape_;

	/**
	 * The stroke to render the outline.
	 */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.</br>
	 * Can be null.
	 */
	public final Paint paint_;

	/**
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public final Paint fill_;

	/**
	 * Transform to be applied to the graphics context.</br>
	 * Never null.
	 */
	public AffineTransform aft_;

	public final Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public ShapeWithStyle(String id, Shape shape, Stroke stroke, Paint paint, Paint fill, Shape clipping, AffineTransform aft)
	{
		this.id_ = id;
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;
		this.clipping_ = clipping;
		this.aft_ = aft == null ? new AffineTransform() : aft;
	}

}
