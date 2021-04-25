package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.graph.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DecoratorVisual implements Visual
{
	final Map<Integer, Decorator> decorators = new HashMap<>();
	final Visual visual;

	@Override
	public void paint(Graphics2D g, Node node)
	{
		Decorator d = decorators.get(node.id);
		if (d != null)
			d.decorate(g, node);
		visual.paint(g, node);
	}

	@Override
	public void paint(Graphics2D g, Edge edge)
	{
		visual.paint(g, edge);
	}

	@Override
	public void setDebug(boolean debug)
	{
		visual.setDebug(debug);
	}

	@Override
	public boolean isDebug()
	{
		return visual.isDebug();
	}

	@Override
	public Rectangle getVisualBounds(Node node)
	{
		Rectangle r = visual.getVisualBounds(node);
		if (!Geometry.isEmpty(r))
		{
			Decorator d = decorators.get(node.id);
			if (d != null)
			{
				Rectangle r2 = d.getBounds(node);
				if (r2 != null)
					Geometry.union(r, r2);
			}
		}
		return r;
	}

	@Override
	public void updateGeometry(Graphics2D g, Node node)
	{
		visual.updateGeometry(g, node);
	}

	@Override
	public Geometry getGeometry()
	{
		return visual.getGeometry();
	}

	@Override
	public Layout getLayout()
	{
		return visual.getLayout();
	}

	@Override
	public int getHorizontalMargin()
	{
		return visual.getHorizontalMargin();
	}

	@Override
	public int getVerticalMargin()
	{
		return visual.getVerticalMargin();
	}

	@Override
	public void setHorizontalMargin(int margin)
	{
		visual.setHorizontalMargin(margin);
	}

	@Override
	public void setVerticalMargin(int margin)
	{
		visual.setVerticalMargin(margin);
	}

	@Override
	public VisualState getVisualState(Node node)
	{
		return visual.getVisualState(node);
	}

	@Override
	public void expand(Node node, boolean expand)
	{
		visual.expand(node, expand);
	}

	@Override
	public void setExpandable(Node node, boolean expandable)
	{
		visual.setExpandable(node, expandable);
	}

	@Override
	public void click(Node node, Point p)
	{
		visual.click(node, p);
	}

	@Override
	public void pressed(Node node, Point p)
	{
		visual.pressed(node, p);
	}

	@Override
	public void released()
	{
		visual.released();
	}


	public DecoratorVisual(Visual visual)
	{
		this.visual = visual;
	}

	public void addDecorator(Node node, Decorator nd)
	{
		Decorator d = decorators.get(node.id);
		if (d != null)
		{
			if (d instanceof DecoratorComposer)
			{
				((DecoratorComposer) d).addDecorator(nd);
			}
			else
			{
				decorators.put(node.id, new DecoratorComposer(d, nd));
			}
		}
		else
		{
			decorators.put(node.id, nd);
		}
		nd.install(visual.getGeometry(), node);
	}

	public void removeDecorator(Node node, Decorator nd)
	{
		Decorator d = decorators.remove(node.id);
		if (d != null)
		{
			if (d instanceof DecoratorComposer)
			{
				DecoratorComposer dc = (DecoratorComposer) d;
				dc.removeDecorator(nd);
				if (dc.size() > 0)
				{
					decorators.put(node.id, dc);
				}
			}
			else if (d != nd)
			{
				decorators.put(node.id, d);
			}
			nd.uninstall(getGeometry(), node);
		}
	}


}
