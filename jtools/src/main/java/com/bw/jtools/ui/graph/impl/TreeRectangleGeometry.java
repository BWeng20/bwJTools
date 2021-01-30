package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryListener;
import com.bw.jtools.ui.graph.GeometryState;

import java.awt.*;
import java.util.List;
import java.util.*;

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

	Rectangle dirtyArea = null;

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
		if ( s == null ) {
			s = new GeometryState();
			s.visible = true;
			states.put(e.id, s);
		}
		return s;
	}

	@Override
	public void moveTree(Graph g, Node node, int dx, int dy)
	{
		beginUpdate();
		if (node != null)
		{
			Shape s = getShape(node);
			if (s != null)
			{
				moveSubTreeRelative(g, node, dx, dy);
				updateParentTreeArea(node);
			}
		}
		endUpdate();
	}

	@Override
	public void moveNode(Graph g, Node node, int dx, int dy)
	{
		beginUpdate();
		if (node != null)
		{
			Rectangle r = new Rectangle(getBounds(node));
			dirty(r);
			r.translate(dx, dy);
			dirty(r);
			setShape(node, r);
			updateTreeArea(node);
		}
		endUpdate();
	}

	protected void updateTreeArea(Node node)
	{
		Rectangle r = new Rectangle(getBounds(node));
		for (Iterator<Node> c = node.children(); c.hasNext(); )
		{
			GeometryState state = getGeometryState(c.next());
			if ( state.visible && state.treeArea != null )
				Geometry.union(r, state.treeArea);
		}
		setTreeArea(node, r);
	}

	protected void updateParentTreeArea(Node node)
	{
		for (Iterator<Node> p = node.parents(); p.hasNext(); )
		{
			Node parent = p.next();
			updateTreeArea(parent);
		}
	}

	protected void moveSubTreeRelative(Graph g, Node node, int dx, int dy)
	{
		Rectangle ot = getBounds(node);
		Rectangle o = new Rectangle( ot );

		final int ox2 = o.x + o.width -1;
		final int oy2 = o.y + o.height -1;

		Rectangle r = new Rectangle(o);
		r.translate(dx, dy);
		Node in;
		int tryc = 0;
		ot.width = -1;
		while ((in = getIntersectingNode(g, r)) != null && (++tryc) < 5)
		{
			Rectangle inR = getBounds(in);

			int inRx2 = inR.x + inR.width -1;
			int inRy2 = inR.y + inR.height -1;

			if (o.x > inRx2 && r.x <= inRx2)
				r.x = inRx2+1;
			else if (ox2 <= inR.x && (r.x+r.width) > inR.x)
				r.x = inR.x - o.width;

			if (o.y > inRy2 && r.y < inRy2)
				r.y = inRy2+1;
			else if (oy2 <= inR.y && (r.y+r.height) > inR.y)
				r.y = inR.y - o.height;
		}
		ot.width = o.width;
		if (in == null)
		{
			setBounds(node, r);
		}
		for (Iterator<Node> c = node.children(); c.hasNext(); )
			moveSubTreeRelative(g, c.next(), dx, dy);
		updateTreeArea(node);
	}

	@Override
	public Shape getShape(Node node)
	{
		return getGeometryState(node).shape;
	}

	@Override
	public Rectangle getBounds(Node node)
	{
		return (Rectangle)getGeometryState(node).shape;
	}


	@Override
	public void setShape(Node node, Shape s)
	{
		setBounds(node, s == null ? null : s.getBounds());
	}

	public void setBounds(Node node, Rectangle r)
	{
		GeometryState s = getGeometryState(node);
		Rectangle o = (Rectangle)s.shape;
		s.shape = r;
		if (o != null)
			dirty(o);
		dirty(r);
		notifyDependencies(node);
	}


	@Override
	public Rectangle getTreeArea(Node node)
	{
		return getGeometryState(node).treeArea;
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
		if ( s.visible )
		{
			if (s.shape != null)
			{
				Rectangle r = (Rectangle)s.shape;
				final int x0 = r.x;
				final int y0 = r.y;
				final int x1 = r.x + r.width - 1;
				final int y1 = r.y + r.height - 1;

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
	public void setTreeArea(Node node, Rectangle r)
	{
		GeometryState s = getGeometryState(node);
		Rectangle or = s.treeArea;
		s.treeArea = r;
		if (or == null)
		{
			beginUpdate();
			dirty(r);
			updateParentTreeArea(node);
			notifyDependencies(node);
			endUpdate();
		} else if (!or.equals(r))
		{
			beginUpdate();
			dirty(or);
			dirty(r);
			updateParentTreeArea(node);
			notifyDependencies(node);
			endUpdate();
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
				} else
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
			} else
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
		} else if (update > 0) --update;

	}


	@Override
	public void setVisibility(GraphElement e, boolean visible)
	{
		GeometryState s = getGeometryState(e);
		if ( s.visible != visible )
		{
			s.visible = visible;
			notifyDependencies(e);
		}
	}


	@Override
	public Rectangle getDirtyArea()
	{
		return dirtyArea;
	}

	@Override
	public void resetDirtyArea()
	{
		dirtyArea = null;
	}

	@Override
	public void dirty(Rectangle r)
	{
		if (dirtyArea == null)
			dirtyArea = new Rectangle(r);
		else
			Geometry.union(dirtyArea, r);
	}

	public Node getIntersectingNode(Graph g, Rectangle r)
	{
		Node root = g.getRoot();
		if (root != null)
		{
			GeometryState s = getGeometryState(root);
			if (s.visible && s.treeArea!=null && s.treeArea.intersects(r))
			{
				return getIntersectingNode(root, r);
			}
		}
		return null;
	}

	public Node getIntersectingNode(Node tree, Rectangle r)
	{
		if (getBounds(tree).intersects(r)) return tree;
		for (Iterator<Node> it = tree.children(); it.hasNext(); )
		{
			tree = it.next();
			GeometryState s = getGeometryState(tree);
			if (s.visible && s.treeArea != null && s.treeArea.intersects(r))
			{
				Node m = getIntersectingNode(tree, r);
				if (m != null) return m;
			}
		}
		return null;
	}
}
