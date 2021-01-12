package com.bw.jtools.ui.graph;

import com.bw.jtools.ui.graph.impl.TreeLayout;
import com.bw.jtools.ui.graph.impl.TreeRectangleGeometry;
import com.bw.jtools.ui.graph.impl.DefaultVisual;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class GraphPanel extends JPanel
{
	private Graph graph = new Graph();
	private Geometry geo = new TreeRectangleGeometry();
	private Visual visual = new DefaultVisual(geo);
	private Layout layout = new TreeLayout(geo);
	private boolean dragging = false;
	private Point  graphOrigin = new Point(0,0);
	private	GraphMouseHandler mouseHandler;

	protected final static Stroke clippingDebugStroke = new BasicStroke(1f);

	/**
	 * Counts paint calls. Can be used to show some fps indicator.
	 */
	public int paintCount = 0;


	public GraphPanel() {
		mouseHandler = new GraphMouseHandler(this);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}

	public void setVisual( Visual v, Layout layout ) {
		if ( v == null ) {
			v = new DefaultVisual(new TreeRectangleGeometry());
		}
		this.geo = v.getGeometry();
		this.geo.clear();
		if ( layout == null ) {
			layout = new TreeLayout(this.geo);
		}

		this.layout = layout;
		this.graphOrigin.x = 0;
		this.graphOrigin.y = 0;
		this.visual = v;
	}

	public Visual getVisual()
	{
		return visual;
	}


		@Override
	public void updateUI()
	{
		super.updateUI();

		if ( geo != null )
		{
			geo.clear();
			graphOrigin.x = 0;
			graphOrigin.y = 0;
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Node root = graph.getRoot();

		if ( root != null )
		{
			Graphics2D g2 = null;

			try
			{
				g2 = (Graphics2D)((Graphics2D)g).create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.translate(graphOrigin.x, graphOrigin.y);

				if (geo.getShape(root) == null)
				{
					geo.beginUpdate();
					updateGeometry(g2, root);
					layout.place(root);
					geo.endUpdate();
					updateSize();
				}

				Rectangle clipping = g2.getClipBounds();
				g2.setColor( getBackground() );
				g2.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);

				if ( visual.isDebug())
				{
					g2.setColor(Color.BLUE);
					g2.setStroke(clippingDebugStroke);
					g2.drawRect(clipping.x, clipping.y, clipping.width - 1, clipping.height - 1);
				}
				// clipping = new Rectangle(clipping.x - 1, clipping.y - 1, clipping.width + 2, clipping.height + 2);

				paintTree(g2, clipping, root);
			} finally
			{
				if ( g2 != null ) g2.dispose();
				++paintCount;
			}
		} else {
			Rectangle clipping = g.getClipBounds();
			g.fillRect( clipping.x, clipping.y, clipping.width, clipping.height );
		}
	}

	protected void updateGeometry(Graphics2D g, Node root) {
		visual.updateGeometry( g, root );
		for (Iterator<Node> it = root.children(); it.hasNext(); )
		{
			Node c = it.next();
			updateGeometry( g, c );
		}
	}


	protected void paintTree(Graphics2D g, Rectangle area, Node n) {

		Rectangle r = geo.getTreeArea(n);
		if ( r.intersects(area))
		{
			if ( visual.getVisualBounds(n).intersects(area))
				visual.paint(g, n);
			if ( visual.isExpanded(n))
			{
				for (Edge e : n.edges)
				{
					if (e.source == n)
					{
						visual.paint(g, e);
						if (!e.cylic && e.target != n)
						{
							paintTree(g, area, e.target);
						}
					}
				}
			}
		}
	}

	protected Node getNodeAt(Node root, Point p)
	{
		if ( geo.getTreeArea(root).contains(p)) {
			if ( geo.getShape(root).contains(p) ) {
				return root;
			}
			else if ( visual.isExpanded(root)) {
				for ( Edge e : root.edges ) {
					if ( e.target != root)
					{
						Node n = getNodeAt(e.target, p);
						if (n != null)
							return n;
					}
				}
			}
		}
		return null;
	}

	public Node getNodeAt( Point p )
	{
		Node root = graph.getRoot();
		if ( root == null)
			return null;
		else
		{
			Point tp = new Point(p);
			tp.translate(-graphOrigin.x, -graphOrigin.y);
			return getNodeAt(root, tp);
		}
	}

	public Node getNodeAt( int x, int y)
	{
		return getNodeAt( new Point(x,y) );
	}

	public Point getNodeLocation( Node node )
	{
		Rectangle r = visual.getGeometry().getBounds(node);
		if ( r != null )
		{
			Point pt = r.getLocation();
			translateToGraphCoordinates( pt );
			return pt;
		}
		return null;
	}

	public void translateToGraphCoordinates( Point p ) {
		p.translate(graphOrigin.x, graphOrigin.y);
	}


	public void moveNode(Node node, int dx, int dy)
	{
		geo.moveTree( graph, node, dx, dy);
		repaintIfNeeded();
		if ( !dragging ) {
			updateSize();
		}
	}

	public void moveOrigin( int dx, int dy ) {
		graphOrigin.x += dx;
		graphOrigin.y += dy;
		if ( !dragging ) {
			updateSize();
		}
		repaint();
	}


	public Graph getGraph() {
		return graph;
	}

	public void startNodeDrag()
	{
		dragging = true;
	}

	public void endNodeDrag()
	{
		dragging = false;
		updateSize();
	}

	protected void updateSize() {

		Node root = graph.getRoot();
		if ( root != null )
		{
			Rectangle r = geo.getTreeArea(root);

			Dimension d = new Dimension( r.x + graphOrigin.x +r.width + 5, r.y +graphOrigin.y+r.height + 5);
			Dimension s = getSize();
			if ( (d.width > s.width || d.height > s.height ) || ( d.width < (s.width-20) || d.height < (s.height-20)) ) {
				setPreferredSize(d);
				revalidate();
			}
		} else {
			graphOrigin = new Point(0,0);
			setPreferredSize( new Dimension(0,0));
			revalidate();
		}
	}

	public void repaintIfNeeded()
	{
		final Rectangle da = geo.getDirtyArea();
		if ( da != null && !da.isEmpty() )
		{
			Rectangle repaintArea = new Rectangle(da);
			repaintArea.grow(2, 2);
			repaintArea.translate(graphOrigin.x, graphOrigin.y);
			repaint(repaintArea);

			geo.resetDirtyArea();
		}
	}
}
