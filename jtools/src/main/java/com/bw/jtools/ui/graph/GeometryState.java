package com.bw.jtools.ui.graph;

import java.awt.geom.Rectangle2D;

/**
 * Stores the state of a grahpical element.
 */
public class GeometryState
{
	/**
	 * The outline/Box of the element.
	 */
	public Rectangle2D.Float boundingBox;

	/**
	 * Visible or hidden
	 */
	public boolean visible;
}
