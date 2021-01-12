package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.Node;

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
	public void place(Node node)
	{
		if ( node != null) {
			Rectangle r = geo.getShape(node).getBounds();

			Rectangle tr = calculateSubTree( node );
			int h = tr.height;

			// Correct tree position to calculated offsets
			tr.y = r.y;
			tr.x = r.x;

			int x = r.x + gap_x + r.width;
			int y;
			if ( h > r.height)
			{
				y = r.y;
				r.y += (h - r.height) / 2;
				geo.setShape(node, r);
			} else {
				int sh = 0;
				for (Iterator<Node> c = node.children(); c.hasNext(); )
				{
					sh += geo.getTreeArea( c.next() ).height+gap_y;
				}
				y = r.y + ((h-sh+gap_y)/2);
			}

			for (Iterator<Node> c = node.children(); c.hasNext(); )
			{
				Node cn = c.next();
				Rectangle rt = geo.getShape(cn).getBounds();
				rt.x = x;
				rt.y = y;
				geo.setShape(cn, rt);
				y += geo.getTreeArea(cn).height + gap_y;
				place(cn);
			}
		}
	}

	public Rectangle calculateSubTree( Node node ) {

		Rectangle r = geo.getTreeArea( node );
		if ( r == null )
		{
			r = geo.getShape( node ).getBounds();
			int h = 0;
			int w = 0;
			for (Iterator<Node> c = node.children(); c.hasNext(); )
			{
				Node cn = c.next();
				if (geo.getShape(cn) != null)
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
		}

		return r;
	}
}
