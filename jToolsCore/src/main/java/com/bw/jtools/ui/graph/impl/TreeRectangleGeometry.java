package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Connector;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryListener;
import com.bw.jtools.ui.graph.VisualState;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages node geometry as rectangle-shapes. This means "shape" equals
 * "bounds". The geometric "tree" relation uses the default tree
 * "child"-relation. All descendants are considered as part of the tree.
 */
public class TreeRectangleGeometry implements Geometry
{

	int margin_y = 5;
	int margin_x = 5;
	int update = 0;
	HashMap<GeometryListenerEntry, List<GraphElement>> toUpdate = new HashMap<>();

	HashMap<Integer, VisualState> states = new HashMap<>();

	Rectangle2D.Float dirtyArea = null;

	protected static class GeometryListenerEntry
	{

		final GeometryListener listener;
		final int hashCode;

		GeometryListenerEntry(GeometryListener l)
		{
			this.listener = l;
			this.hashCode = java.lang.System.identityHashCode(l);
		}

		@Override
		public boolean equals(Object o)
		{
			return this.listener == ((GeometryListenerEntry) o).listener;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}
	}

	private Map<GeometryListenerEntry, GeometryListenerEntry> geoListenerById = new HashMap<>();
	private Map<Integer, List<GeometryListenerEntry>> geoListener = new HashMap<>();

	@Override
	public VisualState getVisualState(GraphElement e)
	{
		VisualState s = states.get(e.id);
		if (s == null)
		{
			s = new VisualState();
			s.visible = true;
			s.connectors = new HashMap<>();
			states.put(e.id, s);
		}
		return s;
	}

	@Override
	public Point2D.Float getConnectorPoint(Node n1, Node n2)
	{
		VisualState gs1 = getVisualState(n1);
		Connector vc = gs1.connectors.get(n2.id);
		if (vc == null)
		{
			vc = new Connector(n1.id);

			VisualState gs2 = getVisualState(n2);

			final Rectangle2D.Float b1 = gs1.boundingBox;
			final Rectangle2D.Float b2 = gs2.boundingBox;


			Point2D.Float p = new Point2D.Float();
			if (b1 == null || b2 == null)
				return null;

			if (b1.x < b2.x)
			{
				vc.xOffset = b1.width;
			}
			else
			{
				vc.xOffset = 0;
			}

			vc.yOffset = b1.height / 2;

			gs1.connectors.put(n2.id, vc);
		}

		return new Point2D.Float(gs1.boundingBox.x + vc.xOffset, gs1.boundingBox.y + vc.yOffset);
	}

	@Override
	public void moveTree(Graph g, Node node, double dx, double dy)
	{
		beginUpdate();
		if (node != null)
		{
			moveSubTreeRelative(g, node, dx, dy);
		}
		endUpdate();
	}

	@Override
	public void moveElement(Graph g, GraphElement element, double dx, double dy)
	{
		beginUpdate();
		if (element instanceof Node)
		{
			Node node = (Node) element;
			Rectangle2D.Float r = new Rectangle2D.Float();
			r.setRect(getBounds(node));
			Geometry.translate(r, dx, dy);
			setBounds(node, r);
		}
		else if (element instanceof Connector)
		{
			final Connector c = (Connector) element;
			c.xOffset = (float) dx;
			c.yOffset = (float) dy;

			VisualState vs = states.get(c.parentId);
			if (vs != null)
			{
				// @TODO: Optimize this
				g.getRoot()
				 .walkTreeNodes((t) ->
				 {
					 if (t.id == c.parentId)
					 {
						 setBounds(t, vs.boundingBox);
						 return false;
					 }
					 return true;
				 });
			}
		}
		endUpdate();
	}

	protected void moveSubTreeRelative(Graph g, Node node, double dx, double dy)
	{
		Rectangle2D.Float ot = getBounds(node);
		Rectangle2D.Float o = new Rectangle2D.Float();
		o.setRect(ot);

		final float ox2 = o.x + o.width - 1;
		final float oy2 = o.y + o.height - 1;

		Rectangle2D.Float r = new Rectangle2D.Float();
		r.setRect(o);
		Geometry.translate(r, dx, dy);
		Node in;
		int tryc = 0;
		ot.width = -1;
		while ((in = getIntersectingNode(g, r)) != null && (++tryc) < 5)
		{
			Rectangle2D.Float inR = getBounds(in);

			float inRx2 = inR.x + inR.width - 1;
			float inRy2 = inR.y + inR.height - 1;

			if (o.x > inRx2 && r.x <= inRx2)
			{
				r.x = inRx2 + 1;
			}
			else if (ox2 <= inR.x && (r.x + r.width) > inR.x)
			{
				r.x = inR.x - o.width;
			}

			if (o.y > inRy2 && r.y < inRy2)
			{
				r.y = inRy2 + 1;
			}
			else if (oy2 <= inR.y && (r.y + r.height) > inR.y)
			{
				r.y = inR.y - o.height;
			}
		}
		ot.width = o.width;
		if (in == null)
		{
			setBounds(node, r);
		}
		for (Iterator<Node> c = node.children(); c.hasNext(); )
		{
			moveSubTreeRelative(g, c.next(), dx, dy);
		}
	}

	@Override
	public Rectangle2D.Float getBounds(Node node)
	{
		return getVisualState(node).boundingBox;
	}

	@Override
	public void setBounds(Node node, Rectangle2D.Float r)
	{
		VisualState s = getVisualState(node);
		Rectangle2D.Float o = s.boundingBox;
		if (o == null)
		{
			s.boundingBox = new Rectangle2D.Float();
		}
		else if (s.visible)
		{
			dirty(o);
		}
		s.boundingBox.setRect(r);
		if (s.visible)
		{
			dirty(r);
			for (Iterator<Edge> e = node.outgoing(true); e.hasNext(); )
			{
				Edge edge = e.next();
				Connector c = s.connectors.get(edge.getTarget().id);
				if (c != null && c.expanded)
					dirty(getBounds(edge.getTarget()));
			}

			for (Iterator<Edge> e = node.incoming(); e.hasNext(); )
				dirty(getBounds(e.next()
								 .getSource()));
		}
		notifyDependencies(node);
	}

	@Override
	public List<Point> getTreePoints(Node node)
	{
		List<Point> points = new ArrayList<>(10);
		addTreePoints(points, node);
		return points;
	}

	@Override
	public List<Point> getTreeDescendantPoints(Node node)
	{
		List<Point> points = new ArrayList<>(10);
		addTreeDescendantPoints(points, node);
		return points;

	}

	private void addTreeDescendantPoints(List<Point> points, Node node)
	{
		for (Iterator<Node> c = node.children(); c.hasNext(); )
		{
			addTreePoints(points, c.next());
		}
	}

	private void addTreePoints(List<Point> points, Node node)
	{
		VisualState s = getVisualState(node);
		if (s.visible)
		{
			if (s.boundingBox != null)
			{
				final Rectangle2D.Float r = s.boundingBox;
				final int x0 = (int) r.x;
				final int y0 = (int) r.y;
				final int x1 = (int) (r.x + r.width - 1);
				final int y1 = (int) (r.y + r.height - 1);

				points.add(new Point(x0, y0));
				points.add(new Point(x1, y0));
				points.add(new Point(x1, y1));
				points.add(new Point(x0, y1));
			}
			for (Iterator<Node> c = node.children(); c.hasNext(); )
			{
				addTreePoints(points, c.next());
			}
		}

	}

	@Override
	public void remove(GraphElement e)
	{
		states.remove(e.id);
		geoListener.remove(e.id);
	}

	@Override
	public void clear()
	{
		states.clear();
		resetDirtyArea();
	}

	protected GeometryListenerEntry getGeometryListenerEntry(GeometryListener l)
	{
		return geoListenerById.get(new GeometryListenerEntry(l));
	}

	@Override
	public void addDependency(GeometryListener l, List<? extends GraphElement> ea)
	{
		if (!ea.isEmpty())
		{
			GeometryListenerEntry key = new GeometryListenerEntry(l);
			GeometryListenerEntry le = geoListenerById.get(key);
			if (le == null)
			{
				le = key;
				geoListenerById.put(key, key);
			}

			for (GraphElement e : ea)
			{
				List<GeometryListenerEntry> ll = geoListener.get(e.id);
				if (ll == null)
				{
					ll = new ArrayList<>();
					ll.add(le);
					geoListener.put(e.id, ll);
				}
				else
				{
					ll.remove(le);
					ll.add(le);
				}
			}
		}
	}

	@Override
	public void removeDependency(GeometryListener l, List<? extends GraphElement> ea)
	{
		if (!ea.isEmpty())
		{
			GeometryListenerEntry le = geoListenerById.get(new GeometryListenerEntry(l));
			if (le != null)
			{
				for (GraphElement e : ea)
				{
					List<GeometryListenerEntry> ll = geoListener.get(e.id);
					if (ll != null)
					{
						ll.remove(le);
					}
				}
			}
		}
	}

	@Override
	public void notifyDependencies(GraphElement e)
	{
		List<GeometryListenerEntry> ll = geoListener.get(e.id);
		if (ll != null)
		{
			if (update > 0)
			{
				for (GeometryListenerEntry gle : ll)
				{
					toUpdate.computeIfAbsent(gle, k -> new ArrayList<>())
							.add(e);
				}
			}
			else
			{
				if (e instanceof Node)
				{
					Node n = (Node) e;
					for (GeometryListenerEntry gl : ll)
					{
						gl.listener.geometryUpdated(this, n);
					}
				}
			}
		}
	}

	public void beginUpdate()
	{
		++update;
	}

	public void endUpdate()
	{
		if (update == 1)
		{
			int iteration = 0;
			do
			{
				HashMap<GeometryListenerEntry, List<GraphElement>> ul = new HashMap<>(toUpdate);
				toUpdate.clear();

				for (Map.Entry<GeometryListenerEntry, List<GraphElement>> entry : ul.entrySet())
				{
					entry.getKey().listener.geometryUpdated(this, entry.getValue());
				}
				++iteration;
			} while ((iteration <= 5) && !toUpdate.isEmpty());
			update = 0;
			if (!toUpdate.isEmpty())
			{
				toUpdate.clear();
				//@TODO
			}
		}
		else if (update > 0)
		{
			--update;
		}

	}

	@Override
	public void setVisibility(GraphElement e, boolean visible)
	{
		VisualState s = getVisualState(e);
		if (s.visible != visible)
		{
			s.visible = visible;
			notifyDependencies(e);
		}
	}

	@Override
	public Rectangle2D getDirtyArea()
	{
		return dirtyArea;
	}

	@Override
	public void resetDirtyArea()
	{
		dirtyArea = null;
	}

	@Override
	public void dirty(Rectangle2D r)
	{
		if (dirtyArea == null)
		{
			dirtyArea = new Rectangle2D.Float();
			dirtyArea.setRect(r);
		}
		else
		{
			Rectangle2D.union(dirtyArea, r, dirtyArea);
		}
	}

	public Node getIntersectingNode(Graph g, Rectangle2D r)
	{
		Node root = g.getRoot();
		if (root != null)
		{
			VisualState s = getVisualState(root);
			if (s.visible)
			{
				return getIntersectingNode(root, r);
			}
		}
		return null;
	}

	public Node getIntersectingNode(Node tree, Rectangle2D r)
	{
		if (getBounds(tree).intersects(r))
		{
			return tree;
		}
		for (Iterator<Node> it = tree.children(); it.hasNext(); )
		{
			tree = it.next();
			VisualState s = getVisualState(tree);
			if (s.visible)
			{
				Node m = getIntersectingNode(tree, r);
				if (m != null)
				{
					return m;
				}
			}
		}
		return null;
	}

	@Override
	public Rectangle2D.Float getGraphBounds(Node root)
	{
		Rectangle2D.Float r = getVisualState(root).boundingBox;
		if (r != null)
		{
			Rectangle2D.Float nr = new Rectangle2D.Float();
			nr.setRect(r);
			for (Node c : root.getTreeDescendantNodes())
			{
				VisualState s = getVisualState(c);
				if (s.visible)
				{
					Rectangle2D.union(nr, s.boundingBox, nr);
				}
			}
			return nr;
		}
		return null;
	}

}
