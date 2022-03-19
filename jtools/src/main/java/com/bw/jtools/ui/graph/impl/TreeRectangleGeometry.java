package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryListener;
import com.bw.jtools.ui.graph.GeometryState;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages node geometry as rectangle-shapes. This means "shape" equals "bounds".
 * The geometric "tree" relation uses the default tree "child"-relation.
 * All descendants are considered as part of the tree.
 */
public class TreeRectangleGeometry implements Geometry
{
	int margin_y = 5;
	int margin_x = 5;
	int update = 0;
	HashMap<GeometryListenerEntry, List<GraphElement>> toUpdate = new HashMap<>();

	HashMap<Integer, GeometryState> states = new HashMap<>();

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
	public GeometryState getGeometryState(GraphElement e)
	{
		GeometryState s = states.get(e.id);
		if (s == null)
		{
			s = new GeometryState();
			s.visible = true;
			states.put(e.id, s);
		}
		return s;
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
	public void moveNode(Graph g, Node node, double dx, double dy, boolean isExanded)
	{
		beginUpdate();
		if (node != null)
		{
			Rectangle2D.Float r = new Rectangle2D.Float();
			r.setRect(getBounds(node));
			Geometry.translate(r, dx, dy);
			setBounds(node, r, isExanded);
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
				r.x = inRx2 + 1;
			else if (ox2 <= inR.x && (r.x + r.width) > inR.x)
				r.x = inR.x - o.width;

			if (o.y > inRy2 && r.y < inRy2)
				r.y = inRy2 + 1;
			else if (oy2 <= inR.y && (r.y + r.height) > inR.y)
				r.y = inR.y - o.height;
		}
		ot.width = o.width;
		if (in == null)
		{
			setBounds(node, r, false);
		}
		for (Iterator<Node> c = node.children(); c.hasNext(); )
			moveSubTreeRelative(g, c.next(), dx, dy);
	}

	@Override
	public Rectangle2D.Float getBounds(Node node)
	{
		return getGeometryState(node).boundingBox;
	}

	@Override
	public void setBounds(Node node, Rectangle2D.Float r, boolean isExpanded)
	{
		GeometryState s = getGeometryState(node);
		Rectangle2D.Float o = s.boundingBox;
		if (o == null)
			s.boundingBox = new Rectangle2D.Float();
		else
			dirty(o);
		s.boundingBox.setRect(r);
		dirty(r);
		for (Edge e : node.edges)
		{
			if ( e.source != node)
				dirty( getBounds(e.source));
			if ( isExpanded && e.target != node)
				dirty( getBounds(e.target));
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
			addTreePoints(points, c.next());
	}

	private void addTreePoints(List<Point> points, Node node)
	{
		GeometryState s = getGeometryState(node);
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
				addTreePoints(points, c.next());
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
					List<GraphElement> gl = toUpdate.computeIfAbsent(gle, k -> new ArrayList<>());
					gl.add(e);
				}
			}
			else
			{
				if (e instanceof Node)
				{
					Node n = (Node) e;
					for (GeometryListenerEntry gl : ll)
						gl.listener.geometryUpdated(this, n);
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
		else if (update > 0) --update;

	}


	@Override
	public void setVisibility(GraphElement e, boolean visible)
	{
		GeometryState s = getGeometryState(e);
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
			Rectangle2D.union(dirtyArea, r, dirtyArea);
	}

	public Node getIntersectingNode(Graph g, Rectangle2D r)
	{
		Node root = g.getRoot();
		if (root != null)
		{
			GeometryState s = getGeometryState(root);
			if (s.visible)
			{
				return getIntersectingNode(root, r);
			}
		}
		return null;
	}

	public Node getIntersectingNode(Node tree, Rectangle2D r)
	{
		if (getBounds(tree).intersects(r)) return tree;
		for (Iterator<Node> it = tree.children(); it.hasNext(); )
		{
			tree = it.next();
			GeometryState s = getGeometryState(tree);
			if (s.visible)
			{
				Node m = getIntersectingNode(tree, r);
				if (m != null) return m;
			}
		}
		return null;
	}

	@Override
	public Rectangle2D.Float getGraphBounds(Node root)
	{
		Rectangle2D.Float r = getGeometryState(root).boundingBox;
		if (r != null)
		{
			Rectangle2D.Float nr = new Rectangle2D.Float();
			nr.setRect(r);
			for (Node c : root.getTreeDescendantNodes())
			{
				Rectangle2D.union(nr, getGeometryState(c).boundingBox, nr);
			}
		}
		return r;
	}

}
