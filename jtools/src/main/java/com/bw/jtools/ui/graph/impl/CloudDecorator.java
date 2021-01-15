package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.geometry.ConvexHull;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Decorator;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryListener;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.*;


public class CloudDecorator implements Decorator, GeometryListener
{
	private int pointDistance = 50;
	private int gap = 3;
	private Stroke stroke = new BasicStroke(2);

	private final Map<Integer, Path2D.Float> paths = new HashMap<>();

	@Override
	public void install(Geometry g, Node node)
	{
		// All sub nodes affect the convix hull so we depend on them
		g.addDependency(this, node.getTreeNodes());
		paths.put(node.id, new Path2D.Float());
		geometryUpdated(g, node);
	}

	@Override
	public void uninstall(Geometry g, Node node)
	{
		paths.remove(node.id);
		g.removeDependency(this, node.getTreeNodes());
	}


	@Override
	public void decorate(Graphics g, Node node)
	{
		Path2D.Float p = paths.get(node.id);
		if (p != null)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(Color.GRAY);
			g2.setStroke(stroke);
			g2.draw(p);
			g2.dispose();
		}
	}

	@Override
	public Rectangle getBounds( Node node )
	{
		Path2D.Float p = paths.get(node.id);
		if (p != null) {
			return p.getBounds();
		}
		return null;
	}

	private void collectRelevantNodes(Set<Node> nodeWithPaths, Node inner)
	{
		if (paths.containsKey(inner.id))
		{
			nodeWithPaths.add(inner);
		}
		for (Iterator<Node> it = inner.parents(); it.hasNext(); )
			collectRelevantNodes(nodeWithPaths, it.next());
	}

	@Override
	public void geometryUpdated(Geometry geo, List<GraphElement> ea)
	{
		Set<Node> toUpdate = new HashSet<>();

		// for all changed nodes collect the parent nodes we decorate.
		for (GraphElement e : ea)
		{
			collectRelevantNodes(toUpdate, (Node) e);
		}

		for (Node node : toUpdate)
		{
			Point[] pts = ConvexHull.convex_hull_graham_andrew(geo.getTreePoints(node));

			if (pts != null && pts.length > 1)
			{
				Path2D.Float oldPath = paths.get(node.id);
				if ( oldPath != null )
				{
					Rectangle b = oldPath.getBounds();
					if ( !b.isEmpty() )
						geo.dirty(b);
				}

				Path2D.Float path = new Path2D.Float();

				final float offset = pointDistance * 0.1f;
				final int N = pts.length;
				float x1, y1, x3, y3, dx, dy, dyy, dxx;


				Point next = pts[0];
				Point last = pts[N-1];
				Point tmp;

				float x0 = last.x-offset;
				float y0 = last.y+offset;
				float x2 = x0;
				float y2 = y0;


				path.moveTo(x0, y0);

				for (int i = 0; i < N; ++i)
				{
					x1 = next.x;
					y1 = next.y;

					if ( i < (N-1))
					{
						tmp = next;
						next = pts[i+1];
						// To get a pretty flow around the corners, we check the slops of the line thought the neighbor points.
						dxx = next.x - last.x;
						dyy = next.y - last.y;
						last = tmp;

						if ( dxx > 0 )
							y1 -= offset;
						else if ( dxx < 0 )
							y1 += offset;
						if ( dyy < 0 )
							x1 -= offset;
						else if ( dyy > 0 )
							x1 += offset;

					} else {
						dxx = dyy = 0;
						x1 -= offset;
						y1 += offset;
					}

					dx = x1 - x0;
					dy = y1 - y0;

					final float length = Math.abs(dx) + Math.abs(dy);
					if (length > pointDistance)
					{
						final int jn = (int)(length / pointDistance);
						final float xf = pointDistance * (dx / length);
						final float yf = pointDistance * (dy / length);

						for (int j = 0; j < jn; ++j)
						{
							if ((j + 2) * pointDistance < length)
							{
								x3 = x0 + (j + 1) * xf;
								y3 = y0 + (j + 1) * yf;
							}
							else
							{
								x3 = x1;
								y3 = y1;
							}
							addQuad(path, x2, y2, x3, y3);
							x2 = x3;
							y2 = y3;
						}
					} else
					{
						addQuad(path, x2, y2, x1, y1);
						x2 = x1;
						y2 = y1;
					}
					x0 = x1;
					y0 = y1;
				}
				paths.put(node.id, path);
				geo.dirty(path.getBounds());
			}
		}
	}

	private void addQuad(Path2D.Float path, float x0, float y0, float x1, float y1)
	{
		final float dx = x1 - x0;
		final float dy = y1 - y0;
		final float length = Math.abs(dx) + Math.abs(dy);
		if (length > 1f)
		{
			path.quadTo(x0 + .5f * dx + pointDistance * (dy / length),
						y0 + .5f * dy - pointDistance * (dx / length), x1, y1);
		}
	}
}
