package com.bw.jtools.ui.graph;

import com.bw.jtools.Log;
import com.bw.jtools.graph.GraphElement;
import com.bw.jtools.graph.Node;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class GraphMouseHandler extends MouseAdapter
{

    /**
     * The current node that is dragged.
     */
    Node nodeDragged;

    /**
     * The current element of the dragged node.
     * If null the whole node is dragged.
     */
    GraphElement elementDragged;

    Point org;
    boolean moved = false;
    Cursor cursor;
    Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    final GraphPanel gpanel;

    public GraphMouseHandler(GraphPanel gpanel)
    {
        this.gpanel = gpanel;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        moved = false;
        nodeDragged = gpanel.getNodeAt(e.getPoint());
        org = e.getPoint();
        elementDragged = null;
        if (nodeDragged != null)
        {
            if (Log.isDebugEnabled())
            {
                Log.debug("Click on node " + nodeDragged.id + " " + org.x + "," + org.y);
            }

            NodeVisual nv = gpanel.getNodeVisual();

            Point2D.Float p = gpanel.getNodeLocation(nodeDragged);

            float scale = nv.getVisualSettings().scale_;
            p.x = (e.getX() - p.x) / scale;
            p.y = (e.getY() - p.y) / scale;
            System.out.println("mousePressed at " + p);

            elementDragged = nv.pressed(nodeDragged, p);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        Log.debug("Mouse released " + e.getPoint());
        if (moved)
        {
            gpanel.endNodeDrag();
            moved = false;
        }
        if (cursor != null)
        {
            gpanel.setCursor(cursor);
            cursor = null;
        }

        gpanel.getNodeVisual()
                .released();

    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (cursor == null)
        {
            cursor = gpanel.getCursor();
            gpanel.setCursor(moveCursor);
        }

        Point p = e.getPoint();

        int dx = p.x - org.x;
        int dy = p.y - org.y;
        org.x = p.x;
        org.y = p.y;

        if (!moved)
        {
            gpanel.startNodeDrag();
            moved = true;
        }

        if (nodeDragged == null)
            gpanel.moveOrigin(dx, dy);
        else if ( elementDragged == null )
            gpanel.moveNode(nodeDragged, dx, dy, !e.isControlDown());            
        else
        {
            gpanel.moveElementAlongShape(elementDragged, gpanel.getNodeVisual().getVisualBounds(nodeDragged), new Point2D.Double(p.x,p.y));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Node node = nodeDragged;
        nodeDragged = null;
        elementDragged = null;
        if (node != null)
        {

            Log.debug("Click @ Node " + node.id);

            Point2D.Float p = gpanel.getNodeLocation(node);
            if (p != null)
            {
                float scale = gpanel.getNodeVisual().getVisualSettings().scale_;
                p.x = (e.getX() - p.x) / scale;
                p.y = (e.getY() - p.y) / scale;
                gpanel.getNodeVisual().click(node, p);
                gpanel.repaintIfNeeded();
            }
        }
    }
}
