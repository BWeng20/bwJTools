package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Node;

public interface Layout
{
	public Geometry getGeometry();

	public void placeChildren(Node node);

}
