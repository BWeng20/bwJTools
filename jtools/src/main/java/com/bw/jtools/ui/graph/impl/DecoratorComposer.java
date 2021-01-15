package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.Decorator;
import com.bw.jtools.ui.graph.Geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DecoratorComposer implements Decorator
{
	private final List<Decorator> decorators = new ArrayList<>(2);

	@Override
	public void decorate(Graphics g, Node node)
	{
		for ( Decorator d : decorators)
			d.decorate( g, node );
	}

	@Override
	public Rectangle getBounds( Node node)
	{
		Rectangle r = null;
		for ( Decorator d : decorators)
		{
			if ( r == null )
				r = d.getBounds(node);
			else
			{
				Rectangle r2 = d.getBounds(node);
				if ( r2 != null )
					Geometry.union(r, r2);
			}
		}
		return r;
	}


	@Override
	public void install(Geometry g, Node node)
	{
		for ( Decorator d : decorators)
			d.install( g, node );
	}

	@Override
	public void uninstall(Geometry g, Node node)
	{
		for ( Decorator d : decorators)
			d.uninstall( g, node );
	}

	public DecoratorComposer( Decorator... d) {
		decorators.addAll(Arrays.asList(d));
	}

	public void addDecorator( Decorator d ) {
		decorators.remove(d);
		decorators.add(d);
	}

	public void removeDecorator(Decorator d)
	{
		decorators.remove(d);
	}

	public int size() {
		return  decorators.size();
	}
}
