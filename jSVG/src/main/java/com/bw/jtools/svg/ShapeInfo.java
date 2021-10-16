package com.bw.jtools.svg;

import java.awt.Shape;
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
	 * The Paint to fill the shape. </br>
	 * Can be null.
	 */
	public PaintWrapper fillWrapper_;

	/**
	 * Transform to be applied to the graphics context.
	 */
	public AffineTransform aft_;

	public Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public ShapeInfo(Shape shape, Stroke stroke, PaintWrapper paint, PaintWrapper fill, Shape clipping)
	{
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paintWrapper_ = paint;
		this.fillWrapper_ = fill;
		this.clipping_ = clipping;
	}

}
