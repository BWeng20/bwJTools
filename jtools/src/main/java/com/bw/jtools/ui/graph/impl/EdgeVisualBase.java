package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.shape.Context;
import com.bw.jtools.ui.graph.EdgeVisual;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.VisualEdgeSettings;
import com.bw.jtools.ui.graph.VisualSettings;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 * Simple line in different modes.
 * @see com.bw.jtools.ui.graph.EdgeMode
 */
public class EdgeVisualBase implements EdgeVisual
{
	protected Geometry geo_;
	protected Layout layout_;
	protected Stroke edgeStroke_;
	protected VisualSettings settings_;

	public EdgeVisualBase(Layout layout, VisualSettings settings)
	{
		this.geo_ = layout.getGeometry();
		this.layout_ = layout;
		this.edgeStroke_ = new BasicStroke(2f);
		this.settings_ = settings;
	}

	public VisualEdgeSettings getVisualSettings()
	{
		return settings_.edge_;
	}

	public Point2D.Float getStartPoint(final Edge edge)
	{
		return geo_.getConnectorPoint(edge.getSource(), edge.getTarget() );
	}

	public Point2D.Float getEndPoint(final Edge edge)
	{
		return geo_.getConnectorPoint(edge.getTarget(), edge.getSource() );
	}

	protected Shape createCurve(final Edge edge)
	{
		final Point2D.Float start = getStartPoint(edge);
		final Point2D.Float end = getEndPoint(edge);

		Shape curve;

		switch (settings_.edge_.mode)
		{
			case STRAIGHT:
				curve = new Line2D.Double(start, end);
				break;
			case BEZIER:
			{
				double x12 = (start.x + end.x) / 2;
				double y12 = (start.y + end.y) / 2;

				Path2D.Double p = new Path2D.Double();

				p.moveTo(start.x, start.y);
				p.quadTo((start.x + x12) / 2, start.y, x12, y12);
				p.quadTo((x12 + end.x) / 2, end.y, end.x, end.y);

				curve = p;
				break;
			}
			case BEZIER_TO_TARGET:
			{
				curve = new QuadCurve2D.Double(start.x, start.y, (start.x + end.x) / 2, end.y, end.x, end.y);
				break;
			}
			case BEZIER_TO_SOURCE:
			{
				curve = new QuadCurve2D.Double(start.x, start.y, (start.x + end.x) / 2, start.y, end.x, end.y);
				break;
			}
			default:
				curve = null;
				break;
		}
		return curve;
	}

	@Override
	public void paint(Context ctx, Edge edge)
	{
		Shape curve = createCurve(edge);
		ctx.g2D_.draw(curve);
	}

	@Override
	public void paintEndPoint(Context ctx, Edge edge)
	{
		// Nothing here
	}

	@Override
	public void paintStartPoint(Context ctx, Edge edge)
	{
		// Nothing here
	}

	@Override
	public void click(Edge node, Point p)
	{
		// @TODO
	}

	@Override
	public void pressed(Edge edge, Point graphPoint)
	{
		// @TODO
	}

	@Override
	public void released()
	{
		// @TODO
	}


}
