package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

public interface Geometry
{
	public void beginUpdate();

	public void endUpdate();

	public VisualState getVisualState(GraphElement e);

	public Point2D.Float getConnectorPoint(Node node1, Node node2);

	/**
	 * Check if node is visible.
	 */
	public default boolean isVisible(GraphElement e)
	{
		return getVisualState(e).visible;
	}

	/**
	 * Sets visibility of a node.
	 */
	public void setVisibility(GraphElement e, boolean visible);

	/**
	 * Gets the bounding rectangle of the node.<br>
	 * Returned object shall NOT be modified.
	 */
	public Rectangle2D.Float getBounds(Node node);

	/**
	 * Sets the bounding rectangle of the node.
	 */
	public void setBounds(Node node, Rectangle2D.Float r);


	/**
	 * Get all points of the shapes of this sub-tree.
	 */
	public List<Point> getTreePoints(Node node);

	/**
	 * Get all points of the shapes of all descendants of this root.
	 */
	public List<Point> getTreeDescendantPoints(Node node);

	public void moveTree(Graph g, Node node, double dx, double dy);

        public void moveElement(Graph g, GraphElement element, double dx, double dy);

	public void clear();

	public void remove(GraphElement e);

	public void addDependency(GeometryListener l, List<? extends GraphElement> e);

	public default void addDependency(GeometryListener l, GraphElement... e)
	{
		addDependency(l, Arrays.asList(e));
	}

	public void removeDependency(GeometryListener l, List<? extends GraphElement> e);

	public default void removeDependency(GeometryListener l, GraphElement... e)
	{
		removeDependency(l, Arrays.asList(e));
	}

	public void notifyDependencies(GraphElement e);

	public void dirty(Rectangle2D e);

	public Rectangle2D getDirtyArea();

	public void resetDirtyArea();

	public Rectangle2D.Float getGraphBounds(Node root);

	/**
	 * Simplified union w/o creating a new object and w/o any range checks.
	 * The union is stored in r1. r1 and r2 needs to be none-negative sized rectangles.
	 */
	public static void union(Rectangle r1, Rectangle r2)
	{
		final int r1x2 = r1.x + r1.width;
		final int r1y2 = r1.y + r1.height;
		final int r2x2 = r2.x + r2.width;
		final int r2y2 = r2.y + r2.height;
		r1.x = (r1.x < r2.x) ? r1.x : r2.x;
		r1.y = (r1.y < r2.y) ? r1.y : r2.y;
		r1.width = ((r1x2 < r2x2) ? r2x2 : r1x2) - r1.x;
		r1.height = ((r1y2 < r2y2) ? r2y2 : r1y2) - r1.y;
	}

	public static void translate(Rectangle2D.Float r, double dx, double dy)
	{
		r.x += dx;
		r.y += dy;
	}

	public static void translate(Rectangle2D.Double r, double dx, double dy)
	{
		r.x += dx;
		r.y += dy;
	}

	public static boolean isEmpty(Rectangle r)
	{
		return r == null || r.isEmpty();
	}

	public static boolean isEmpty(Rectangle2D r)
	{
		return r == null || r.isEmpty();
	}

	/**
	 * Creates Rectangle by simple int-casts.
	 */
	public static Rectangle toRect(Rectangle2D r)
	{
		if (r instanceof Rectangle2D.Float)
			return toRect((Rectangle2D.Float) r);
		else if (r instanceof Rectangle)
			return new Rectangle((Rectangle) r);
		else if (r instanceof Rectangle2D.Double)
			return toRect((Rectangle2D.Double) r);
		else
			return new Rectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Creates Rectangle by simple int-casts.
	 */
	public static Rectangle toRect(Rectangle2D.Float r)
	{
		return new Rectangle((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

	/**
	 * Creates Rectangle by simple int-casts.
	 */
	public static Rectangle toRect(Rectangle2D.Double r)
	{
		return new Rectangle((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

}
