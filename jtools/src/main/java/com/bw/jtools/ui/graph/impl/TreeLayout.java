package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.GeometryState;
import com.bw.jtools.ui.graph.Layout;

import java.awt.*;
import java.util.Iterator;

public class TreeLayout implements Layout
{
	int gap_x = 10;
	int gap_y = 10;
	Geometry geo;

	public TreeLayout(Geometry m ) {
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
		if ( node != null) {
			geo.beginUpdate();

            Rectangle r = geo.getBounds(node);

			Rectangle tr = recalculateSubTree( node );
			int th = tr.height;

			// Correct tree position to calculated offsets
			tr.y = r.y;
			tr.x = r.x;

			int x = r.x + gap_x + r.width;
			int y;
			if ( th > r.height)
			{
				y = r.y - (th-r.height)/2;
			} else {
				int sh = 0;
				for (Iterator<Node> c = node.children(); c.hasNext(); )
				{
					Node cn = c.next();
					GeometryState state = geo.getGeometryState(cn);
					if ( state.visible && state.treeArea != null )
						sh += state.treeArea.height+gap_y;
				}
				y = r.y + ((th-sh+gap_y)/2);
			}

			for (Iterator<Node> c = node.children(); c.hasNext(); )
			{
				Node cn = c.next();
				GeometryState state = geo.getGeometryState(cn);
				if ( state.visible )
				{
					Rectangle rt = state.shape.getBounds();
					rt.x = x;
					rt.y = y;
					geo.setShape(cn, rt);
					this.placeChildren(cn);
					y += state.treeArea.height + gap_y;
				}
			}

			geo.endUpdate();
		}
	}

	public Rectangle calculateSubTree( Node node )
	{
		Rectangle r = geo.getTreeArea(node);
		if (r == null)
		{
			r = recalculateSubTree(node);
		}
		return r;
	}

	public Rectangle recalculateSubTree( Node node ) {
		Rectangle r = new Rectangle( geo.getBounds( node ) );
		int h = 0;
		int w = 0;
		for (Iterator<Node> c = node.children(); c.hasNext(); )
		{
			Node cn = c.next();
			GeometryState state = geo.getGeometryState(cn);
			if (state.visible && state.shape != null)
			{
				Rectangle tr = calculateSubTree(cn);
				h += tr.height + gap_y;
				if (w < tr.width) w = tr.width;
			}
		}
		if ( h > 0 ) h -= gap_y;

		if (h > r.height) r.height = h;
		if (w > 0)
			r.width += gap_x + w;
		geo.setTreeArea(node, r);

		return r;
	}
}
