package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.NodeDecorator;

import java.awt.geom.Rectangle2D;


public class ShapeNodeDecorator implements NodeDecorator
{
	public ShapeNodeDecorator(Geometry geometry, DecoratorShape shape)
	{
		geo = geometry;
		this.shape = shape;
	}

	protected Geometry geo;
	protected DecoratorShape shape;

	@Override
	public void install(Node node)
	{
	}

	@Override
	public void uninstall(Node node)
	{
	}


	@Override
	public void decorate(Context ctx, Node node)
	{
		shape.paintAlong(ctx, geo.getBounds(node), 0, -shape.getDistance());
	}

	@Override
	public Rectangle2D.Float getBounds(Node node)
	{
		Rectangle2D.Float r = new Rectangle2D.Float();
		r.setRect(geo.getBounds(node));
		double w = shape.getDistance() / 2;
		r.x -= w;
		r.y -= w;
		r.width += 2 * w;
		r.height += 2 * w;
		return r;
	}


}
