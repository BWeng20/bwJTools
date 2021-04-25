package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Node;

import java.awt.*;

public interface Visual
{
	public void setDebug(boolean debug);

	public boolean isDebug();

	public void paint(Graphics2D g, Node node);

	public void paint(Graphics2D g, Edge edge);

	/**
	 * Updates the geometry of a node according to its visual properties.
	 */
	public void updateGeometry(Graphics2D g, Node node);

	public Geometry getGeometry();

	public Layout getLayout();

	public int getHorizontalMargin();

	public int getVerticalMargin();

	public void setHorizontalMargin(int margin);

	public void setVerticalMargin(int margin);

	public VisualState getVisualState(Node node);

	/**
	 * Get the rectangle that covers all visuals of it.
	 */
	public Rectangle getVisualBounds(Node n);

	/**
	 * Expand or collapse the node.
	 */
	public void expand(Node node, boolean expand);

	/**
	 * Check expanded state of a node.
	 */
	public default boolean isExpanded(Node node)
	{
		return getVisualState(node).expanded;
	}

	/**
	 * Check expandable state of a node.
	 */
	public default boolean isExpandable(Node node)
	{
		return getVisualState(node).expandable;
	}

	/**
	 * Show or hide the expand icon of the node.
	 */
	public void setExpandable(Node node, boolean expandable);

	/**
	 * Calls if used clicked on the node.
	 * Coordinates are relative to node origin.
	 */
	public void click(Node node, Point p);

	/**
	 * Calls if used clicked on the node.
	 * Coordinates are relative to node origin.
	 */
	public void pressed(Node node, Point p);

	/**
	 * Calls if used release the mouse button.
	 */
	public void released();
}
