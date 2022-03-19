package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.shape.Context;
import com.bw.jtools.svg.ShapeHelper;
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
		super(layout,settings);
	}

	@Override
	public void paintEndPoint(Context ctx, Edge edge)
	{
		if (settings.edge.decorate)
		{
			Point2D.Double p = getEndPoint(edge);
			p.x -= 0.25 * shape.getWidth();
			shape.drawAtPoint(ctx, p, shape.getNumberOfVariants()-1);
		}
	}

	@Override
	public void paintStartPoint(Context ctx, Edge edge)
	{
		if (settings.edge.decorate)
		{
			// Not yet
		}
	}


	@Override
	public void paint(Context ctx, Edge edge)
	{
		if (settings.edge.decorate)
		{
			Shape curve = createCurve(edge);
			if (curve != null)
			{
				ctx.g2D_.setColor(settings.edge.color);
				ctx.g2D_.setStroke(edgeStroke);
				shape.paintAlong(ctx, new ShapeHelper(curve),
						0, -shape.getWidth()*0.5, 0, -2 );
			}
		}
		else
			super.paint(ctx, edge);
	}

}
