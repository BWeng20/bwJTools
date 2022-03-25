package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;

import java.awt.geom.Rectangle2D;

public interface NodeDecorator
{
	public void decorate(Context ctx, Node node);

	public Rectangle2D.Float getBounds(Node node);

	public void install(Node node);

	public void uninstall(Node node);

}
