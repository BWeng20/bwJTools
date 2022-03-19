package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryState;
import com.bw.jtools.ui.graph.Layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class TreeLayout implements Layout
{
	int gap_x = 10;
	int gap_y = 10;
	Geometry geo;

	public TreeLayout(Geometry m)
	{
		geo = m;
	}

	@Override
	public Geometry getGeometry()
	{
		return geo;
	}

	@Override
	public void placeChildren(Node node)
	{
		if (node != null)
		{
			geo.beginUpdate();

			Rectangle2D.Float r = geo.getBounds(node);
			Rectangle2D.Float tr = recalculateSubTree(node);
			float th = tr.height;

			// Correct tree position to calculated offsets
			tr.y = r.y;
			tr.x = r.x;

			float x = r.x + 5*gap_x + r.width;
			float y;
			if (th > r.height)
			{
				y = r.y - (th - r.height) / 2;
			}
			else
			{
				int sh = 0;
				for (Iterator<Node> c = node.children(); c.hasNext(); )
				{
					Node cn = c.next();
					GeometryState state = geo.getGeometryState(cn);
					Rectangle2D cr = geo.getGraphBounds(cn);
					if (state.visible && r != null)
						sh += cr.getHeight() + gap_y;
				}
				y = r.y + ((th - sh + gap_y) / 2);
			}

			for (Iterator<Node> c = node.children(); c.hasNext(); )
			{
				Node cn = c.next();
				GeometryState state = geo.getGeometryState(cn);
				if (state.visible)
				{
					Rectangle2D.Float rn = new Rectangle2D.Float(x,y,state.boundingBox.width, state.boundingBox.height);
					geo.setBounds(cn, rn, false );
					this.placeChildren(cn);
					Rectangle2D cr = geo.getGraphBounds(cn);
					y += cr.getHeight() + gap_y;
				}
			}

			geo.endUpdate();
		}
	}

	public Rectangle2D.Float calculateSubTree(Node node)
	{
		Rectangle2D.Float r = geo.getGraphBounds(node);
		if (r == null)
		{
			r = recalculateSubTree(node);
		}
		return r;
	}

	public Rectangle2D.Float recalculateSubTree(Node node)
	{
		Rectangle2D.Float r = new Rectangle2D.Float();
		r.setRect(geo.getBounds(node));
		float h = 0;
		float w = 0;
		for (Iterator<Node> c = node.children(); c.hasNext(); )
		{
			Node cn = c.next();
			GeometryState state = geo.getGeometryState(cn);
			if (state.visible && state.boundingBox != null)
			{
				Rectangle2D tr = calculateSubTree(cn);
				h += tr.getHeight() + gap_y;
				if (w < tr.getWidth()) w = (float) tr.getWidth();
			}
		}
		if (h > 0) h -= gap_y;

		if (h > r.height) r.height = h;
		if (w > 0)
			r.width += gap_x + w;

		return r;
	}
}
