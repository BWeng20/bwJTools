package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.shape.Context;

import java.awt.Point;

public interface EdgeVisual
{
	public VisualEdgeSettings getVisualSettings();

	/**
	 * Paint the edge.
	 * This method is called <i>before</i> all nodes are painted.
	 */
	public void paint(Context ctx, Edge edge);

	/**
	 * Paint the end point.<br>/
	 * Needed for decorated edges to paint a pretty connection/overlapping.<br>
	 * This method is called <i>after</i> all nodes are painted.
	 */
	public void paintEndPoint(Context ctx, Edge edge);

	/**
	 * Paint the start point.<br>/
	 * Needed for decorated point to paint a pretty connection/overlapping.<br>
	 * This method is called <i>after</i> all nodes are painted.
	 */
	public void paintStartPoint(Context ctx, Edge edge);

	/**
	 * Calls if used clicked on the edge.
	 * Coordinates are relative to edge origin.
	 */
	public void click(Edge edge, Point p);

	/**
	 * Calls if used clicked on the edge.
	 * Coordinates are relative to edge origin.
	 */
	public void pressed(Edge edge, Point p);

	/**
	 * Calls if used release the mouse button.
	 */
	public void released();
}
