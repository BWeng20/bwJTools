package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public interface Geometry
{
	public void beginUpdate();
	public void endUpdate();

	public GeometryState getGeometryState(GraphElement e);

	/**
	 * Check if node is visible.
	 */
	public default boolean isVisible(GraphElement e )
	{
		return getGeometryState(e).visible;
	}

	/**
	 * Sets visibility of a node.
	 */
	public void setVisibility(GraphElement e, boolean visible );

	/**
	 * Gets the shape of the node.
	 */
	public Shape getShape(Node node );

	/**
	 * Sets the shape of the node.
	 */
	public void setShape( Node node, Shape s );

	/**
	 * Gets the bounding rectangle of the node.
	 */
	public Rectangle getBounds( Node node );

	/**
	 * Sets the area rectangle of the sub-tree.
	 */
	public void setTreeArea(Node node, Rectangle r );

	/**
	 * Gets the area rectangle of the sub-tree.
	 * Any change to the returned rectangle will be reflected in the geometry!
	 */
	public Rectangle getTreeArea(Node node );

	/**
	 * Get all points of the shapes of this sub-tree.
	 */
	public List<Point> getTreePoints(Node node);

	/**
	 * Get all points of the shapes of all descendants of this root.
	 */
	public List<Point> getTreeDescendantPoints(Node node);

	public void moveTree(Graph g, Node node, int dx, int dy);

	public void moveNode(Graph g,Node node, int dx, int dy);

	public void clear();

	public void remove(GraphElement e);

	public void addDependency(GeometryListener l, List<? extends GraphElement> e) ;
	public default void addDependency(GeometryListener l, GraphElement... e) {
		addDependency( l, Arrays.asList(e));
	}

	public void removeDependency(GeometryListener l, List<? extends GraphElement> e) ;
	public default void removeDependency(GeometryListener l, GraphElement... e) {
		removeDependency( l, Arrays.asList(e));
	}

	public void	notifyDependencies(GraphElement e);

	public void dirty(Rectangle e );
	public Rectangle getDirtyArea();
	public void resetDirtyArea();

	public default void animateTree(Node root, Geometry source, Geometry target, double amount ) {
		if ( root != null )
		{
			animateNode(root, source, target, amount);
			for (Iterator<Node> cit = root.children(); cit.hasNext(); )
			{
				animateTree(cit.next(), source, target, amount);
			}
		}
	}


	public default void animateNode( Node node, Geometry source, Geometry target, double amount ) {

		Rectangle ts = source.getTreeArea(node);
		Rectangle tt = target.getTreeArea(node);

		if ( ts != null && tt != null ) {
			Rectangle r = getTreeArea(node);

			final int x = ts.x + (int)(0.5+(tt.x-ts.x)*amount);
			final int y = ts.y + (int)(0.5+(tt.y-ts.y)*amount);
			final int w = ts.width + (int)(0.5+(tt.width-ts.width)*amount);
			final int h = ts.height+ (int)(0.5+(tt.height-ts.height)*amount);

			if ( r == null ) {
				r = new Rectangle(x,y, w, h);
				setTreeArea(node, r);
			} else {
				r.x = x;
				r.y = y;
				r.width = w;
				r.height = h;
			}
		}
	}

	/**
	 * Simplified union w/o creating a new object and w/o any range checks.
	 * The union is stored in r1. r1 and r2 needs to be none-negative sized rectangles.
	 */
	public static void union( Rectangle r1, Rectangle r2) {
		final int r1x2 = r1.x + r1.width ;
		final int r1y2 = r1.y + r1.height ;
		final int r2x2 = r2.x + r2.width ;
		final int r2y2 = r2.y + r2.height ;
		r1.x = (r1.x < r2.x) ? r1.x : r2.x;
		r1.y = (r1.y < r2.y) ? r1.y : r2.y;
		r1.width = ((r1x2 < r2x2) ? r2x2 : r1x2 ) - r1.x;
		r1.height = ((r1y2 < r2y2) ? r2y2 : r1y2 ) - r1.y;
	}

	public static boolean isEmpty(Rectangle r)
	{
		return r == null || r.isEmpty();
	}


}
