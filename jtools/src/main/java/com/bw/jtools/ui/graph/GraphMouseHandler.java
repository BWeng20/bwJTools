package com.bw.jtools.ui.graph;

import com.bw.jtools.Log;
import com.bw.jtools.graph.Node;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class GraphMouseHandler extends MouseAdapter
{
	Node nodeDragged;
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
		if (nodeDragged != null)
		{
			if (Log.isDebugEnabled()) Log.debug("Click on Node " + nodeDragged.id + " " + org.x + "," + org.y);

			Point2D.Float p = gpanel.getNodeLocation(nodeDragged);
			float scale = gpanel.getNodeVisual().getVisualSettings().scale;
			p.x = (e.getX() - p.x) / scale;
			p.y = (e.getY() - p.y) / scale;
			System.out.println("mousePressed at "+p);
			gpanel.getNodeVisual()
				  .pressed(nodeDragged, p);
		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		Log.debug("Mouse released "+e.getPoint());
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
		{
			gpanel.moveOrigin(dx, dy);
		}
		else
		{
			gpanel.moveNode(nodeDragged, dx, dy, !e.isControlDown());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Node node = nodeDragged;
		nodeDragged = null;
		if (node != null)
		{

			Log.debug("Click @ Node " + node.id);

			Point2D.Float p = gpanel.getNodeLocation(node);
			if (p != null)
			{
				float scale = gpanel.getNodeVisual().getVisualSettings().scale;
				p.x = (e.getX() - p.x) / scale;
				p.y = (e.getY() - p.y) / scale;
				gpanel.getNodeVisual().click(node, p);
				gpanel.repaintIfNeeded();
			}
		}
	}
}