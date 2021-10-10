package com.bw.jtools.shape;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * A shape plus additional style information.
 */
public final class ShapeWithStyle
{
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

	public final Shape clipping_;

	/**
	 * Constructor to initialize,
	 */
	public ShapeWithStyle(String id, Shape shape, Stroke stroke, Paint paint, Paint fill, Shape clipping)
	{
		this.id_ = id;
		this.shape_ = shape;
		this.stroke_ = stroke;
		this.paint_ = paint;
		this.fill_ = fill;

		this.clipping_ = clipping;
	}

}
