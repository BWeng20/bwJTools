package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.GraphElement;

import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Stores the state of a grahpical element.
 */
public class GeometryState
{
	/**
	 * The outline/Box of the element.
	 */
	public Rectangle2D.Float boundingBox;

	public Map<Integer, VisualConnector> connectors;

	/**
	 * Visible or hidden
	 */
	public boolean visible;
}
