package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.geometry.OrthogonalPoint;
import com.bw.jtools.graph.Edge;
import com.bw.jtools.shape.Context;
import com.bw.jtools.svg.ShapeHelper;
import com.bw.jtools.svg.ShapeHelper.Segment;
import com.bw.jtools.ui.graph.EdgeVisual;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.VisualEdgeSettings;
import com.bw.jtools.ui.graph.VisualSettings;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 * Simple line in different modes.
 *
 * @see com.bw.jtools.ui.graph.EdgeMode
 */
public class EdgeVisualBase implements EdgeVisual
{

    protected Geometry geo_;
    protected Layout layout_;
    protected VisualSettings settings_;
    protected static final Shape defaultSnakePath_;
    
    static 
    {
        Path2D p = new Path2D.Double();
        p.moveTo(0, 0);
        p.curveTo(0,0.3, .5,0.3, 0.5,0);
        p.curveTo(0.5,-0.3, 1,-0.3, 1,0);
        
        defaultSnakePath_ = p;
    }

    public EdgeVisualBase(Layout layout, VisualSettings settings)
    {
        this.geo_ = layout.getGeometry();
        this.layout_ = layout;
        this.settings_ = settings;
    }

    @Override
    public VisualEdgeSettings getVisualSettings()
    {
        return settings_.edge_;
    }

    public Point2D.Float getStartPoint(final Edge edge)
    {
        return geo_.getConnectorPoint(edge.getSource(), edge.getTarget());
    }

    public Point2D.Float getEndPoint(final Edge edge)
    {
        return geo_.getConnectorPoint(edge.getTarget(), edge.getSource());
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
        if (curve != null && settings_.edge_.snakeFactor > 0)
        {
            final double length = start.distance(end);
            Shape snake  = null == settings_.edge_.snakePath ? 
                            defaultSnakePath_: settings_.edge_.snakePath;
            
            snake = AffineTransform.getScaleInstance(length, length).createTransformedShape(snake);
            ShapeHelper snakeHelper = new ShapeHelper(snake);
                    
            PathIterator pi = curve.getPathIterator(null, 0.1d);
            double x = 0;
            double y = 0;
            double cx = 0;
            double cy = 0;
            Point2D.Double p = new Point2D.Double();
            double[] seg = new double[6];

            Path2D.Float path = new Path2D.Float();
            double d = 0;

            while (!pi.isDone())
            {
                final int type = pi.currentSegment(seg);
                switch (type)
                {
                    case PathIterator.SEG_MOVETO:
                        x = seg[0];
                        y = seg[1];
                        cx = x;
                        cy = y;
                        path.moveTo(x, y);
                        break;
                    case PathIterator.SEG_LINETO:
                        double xd = Point2D.Double.distance(x, y, seg[0], seg[1]);
                        double id = 0;
                        double dx = seg[0] - x;
                        double dy = seg[1] - y;
                        double x2;
                        double y2;
                        while( id < xd )
                        {
                            id += 10;
                            if ( id > xd )
                                id = xd;
                            
                            double f = id / xd;
                            x2 = x + dx * f;
                            y2 = y + dy * f;
                       
                            Segment sgt = snakeHelper.findSegmentAtX(d+id);
                            if ( sgt != null )
                            {
                                double sd = sgt.y_ * settings_.edge_.snakeFactor;
                                OrthogonalPoint.get(x, y,x2, y2, sd,  p);
                            }
                            else
                            {
                                p.x = x2;
                                p.y = y2;
                            }
                            path.lineTo(p.x, p.y);
                        }
                        x = seg[0];
                        y = seg[1];
                        d += xd;
                       
                        break;
                    case PathIterator.SEG_CLOSE:
                        path.closePath();
                        x = cx;
                        y = cy;
                        break;
                }
                pi.next();
            }
            curve = path;
        }
        return curve;
    }

    @Override
    public float getMaxWidth()
    {
        return settings_.edge_.width;
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
