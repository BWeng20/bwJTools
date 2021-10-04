package com.bw.jtools.svg;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * Collectgs all information about a shape that are needed to produce a final shape.
 */
public final class ShapeInfo
{
	/**
	 * Id to identify the shape in the some document.
	 */
	public String id_;

	/**
	 * The shape.
	 */
	public Shape shape_;

	/**
	 * The stroke to render the outline.
	 */
	public Stroke stroke_;

	/**
	 * The Paint to render the outline.</br>
	 * Can be null.
	 */
	public PaintWrapper paintWrapper_;

	/**
	 * Overall Stroke opacity. Is equal to stroke-opacity * opacity
	 */
	public float strokeOpacity_;

	/**
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public PaintWrapper fillWrapper_;

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
	public ShapeInfo(Shape shape, Stroke stroke, PaintWrapper paint,
					 float strokeOpacity, PaintWrapper fill, float fillOpacity, Shape clipping)
	{
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paintWrapper_ = paint;
		this.fillWrapper_ = fill;
		this.strokeOpacity_ = strokeOpacity;
		this.fillOpacity_ = fillOpacity;
		this.clipping_ = clipping;
	}

}
