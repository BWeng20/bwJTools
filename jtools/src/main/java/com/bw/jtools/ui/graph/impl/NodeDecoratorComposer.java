package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;
import com.bw.jtools.ui.graph.NodeDecorator;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Aggregates multiple node decorators.
 */
public class NodeDecoratorComposer implements NodeDecorator
{
	private final List<NodeDecorator> decorators = new ArrayList<>(2);

	@Override
	public void decorate(Context ctx, Node node)
	{
		for (NodeDecorator d : decorators)
			d.decorate(ctx, node);
	}

	@Override
	public Rectangle2D getBounds(Node node)
	{
		Rectangle2D r = null;
		for (NodeDecorator d : decorators)
		{
			if (r == null)
				r = d.getBounds(node);
			else
			{
				Rectangle2D r2 = d.getBounds(node);
				if (r2 != null)
					Rectangle2D.union(r, r2, r);
			}
		}
		return r;
	}


	@Override
	public void install(Node node)
	{
		for (NodeDecorator d : decorators)
			d.install(node);
	}

	@Override
	public void uninstall(Node node)
	{
		for (NodeDecorator d : decorators)
			d.uninstall(node);
	}

	public NodeDecoratorComposer(NodeDecorator... d)
	{
		decorators.addAll(Arrays.asList(d));
	}

	public void addDecorator(NodeDecorator d)
	{
		decorators.remove(d);
		decorators.add(d);
	}

	public void removeDecorator(NodeDecorator d)
	{
		decorators.remove(d);
	}

	public int size()
	{
		return decorators.size();
	}
}
