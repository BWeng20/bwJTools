package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.NodeDecorator;
import com.bw.jtools.ui.graph.NodeVisual;
import com.bw.jtools.ui.graph.VisualSettings;
import com.bw.jtools.ui.graph.VisualState;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class DecoratorNodeVisual implements NodeVisual
{
	final Map<Integer, NodeDecorator> decorators = new HashMap<>();
	final NodeVisual nodeVisual;

	@Override
	public void paint(Context ctx, Node node)
	{
		NodeDecorator d = decorators.get(node.id);
		if (d != null)
			d.decorate(ctx, node);
		nodeVisual.paint(ctx, node);
	}

	@Override
	public VisualSettings getVisualSettings()
	{
		return nodeVisual.getVisualSettings();
	}

	@Override
	public Rectangle2D getVisualBounds(Node node)
	{
		Rectangle2D r = nodeVisual.getVisualBounds(node);
		if (!Geometry.isEmpty(r))
		{
			NodeDecorator d = decorators.get(node.id);
			if (d != null)
			{
				Rectangle2D r2 = d.getBounds(node);
				if (r2 != null)
				{
					Rectangle2D.Float rn = new Rectangle2D.Float();
					Rectangle.union(r, r2, rn);
					r = rn;
				}
			}
		}
		return r;
	}

	@Override
	public void updateGeometry(Graphics2D g, Node node)
	{
		nodeVisual.updateGeometry(g, node);
	}

	@Override
	public Geometry getGeometry()
	{
		return nodeVisual.getGeometry();
	}

	@Override
	public Layout getLayout()
	{
		return nodeVisual.getLayout();
	}

	@Override
	public int getHorizontalMargin()
	{
		return nodeVisual.getHorizontalMargin();
	}

	@Override
	public int getVerticalMargin()
	{
		return nodeVisual.getVerticalMargin();
	}

	@Override
	public void setHorizontalMargin(int margin)
	{
		nodeVisual.setHorizontalMargin(margin);
	}

	@Override
	public void setVerticalMargin(int margin)
	{
		nodeVisual.setVerticalMargin(margin);
	}

	@Override
	public VisualState getVisualState(Node node)
	{
		return nodeVisual.getVisualState(node);
	}

	@Override
	public void expand(Node node, boolean expand)
	{
		nodeVisual.expand(node, expand);
	}

	@Override
	public void setExpandable(Node node, boolean expandable)
	{
		nodeVisual.setExpandable(node, expandable);
	}

	@Override
	public void click(Node node, Point2D p)
	{
		nodeVisual.click(node, p);
	}

	@Override
	public void pressed(Node node, Point2D p)
	{
		nodeVisual.pressed(node, p);
	}

	@Override
	public void released()
	{
		nodeVisual.released();
	}


	public DecoratorNodeVisual(NodeVisual nodeVisual)
	{
		this.nodeVisual = nodeVisual;
	}

	public void addDecorator(Node node, NodeDecorator nd)
	{
		NodeDecorator d = decorators.get(node.id);
		if (d != null)
		{
			if (d instanceof NodeDecoratorComposer)
			{
				((NodeDecoratorComposer) d).addDecorator(nd);
			}
			else
			{
				decorators.put(node.id, new NodeDecoratorComposer(d, nd));
			}
		}
		else
		{
			decorators.put(node.id, nd);
		}
		nd.install(node);
	}

	public void removeDecorator(Node node, NodeDecorator nd)
	{
		NodeDecorator d = decorators.remove(node.id);
		if (d != null)
		{
			if (d instanceof NodeDecoratorComposer)
			{
				NodeDecoratorComposer dc = (NodeDecoratorComposer) d;
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
			nd.uninstall(node);
		}
	}


}
