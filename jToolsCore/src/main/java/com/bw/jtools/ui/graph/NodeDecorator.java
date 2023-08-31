package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Node;

import java.awt.geom.Rectangle2D;

public interface NodeDecorator
{
	public void decorate(GraphicContext ctx, Node node);

	public Rectangle2D.Float getBounds(Node node);

	public void install(Node node);

	public void uninstall(Node node);

}
