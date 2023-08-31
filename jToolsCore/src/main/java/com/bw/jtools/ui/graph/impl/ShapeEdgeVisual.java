package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.svg.ShapeHelper;
import com.bw.jtools.ui.graph.DecoratorShape;
import com.bw.jtools.ui.graph.GraphicContext;
import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.VisualSettings;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Edge painted with shapes, along the edge provided by base class.
 */
public class ShapeEdgeVisual extends EdgeVisualBase
{

	DecoratorShape shape = DecoratorShape.LEAVES_BW;

	public ShapeEdgeVisual(Layout layout, VisualSettings settings)
	{
		super(layout, settings);
	}

	@Override
	public void paintEndPoint(GraphicContext ctx, Edge edge)
	{
		if (settings_.edge_.decorate)
		{
			Point2D.Float p = getEndPoint(edge);
			p.x -= 0.25 * shape.getWidth();
			shape.drawAtPoint(ctx, p, shape.getNumberOfVariants() - 1);
		}
	}

	@Override
	public void paintStartPoint(GraphicContext ctx, Edge edge)
	{
		if (settings_.edge_.decorate)
		{
			// Not yet
		}
	}

	@Override
	public float getMaxWidth()
	{
		if (settings_.edge_.decorate)
		{
			return (float) shape.getWidth();
		}
		else
		{
			return super.getMaxWidth();
		}
	}

	@Override
	public void paint(GraphicContext ctx, Edge edge)
	{
		if (settings_.edge_.decorate)
		{
			Shape curve = createCurve(edge);
			if (curve != null)
			{
				shape.paintAlong(ctx, new ShapeHelper(curve),
						0, -shape.getWidth() * 0.5, 0, -2);
			}
		}
		else
		{
			super.paint(ctx, edge);
		}
	}

}
