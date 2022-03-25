package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Attribute;
import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface NodeVisual
{
	public static final Attribute NODE_TEXT = Attribute.getAttribute("text");

	public void paint(Context ctx, Node node);

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
	 * Get the rectangle that covers all visuals of it.<br>
	 * Returned object shall NOT be modified.
	 */
	public Rectangle2D.Float getVisualBounds(Node n);

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
	public void click(Node node, Point2D p);

	/**
	 * Calls if used clicked on the node.
	 * Coordinates are relative to node origin.
	 */
	public void pressed(Node node, Point2D p);

	/**
	 * Calls if used release the mouse button.
	 */
	public void released();

	VisualSettings getVisualSettings();
}
