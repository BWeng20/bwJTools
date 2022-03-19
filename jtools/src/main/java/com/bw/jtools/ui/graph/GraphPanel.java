package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.Node;
import com.bw.jtools.shape.Context;
import com.bw.jtools.ui.graph.impl.NodeLabelVisual;
import com.bw.jtools.ui.graph.impl.TreeLayout;
import com.bw.jtools.ui.graph.impl.TreeRectangleGeometry;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class GraphPanel extends JPanel
{
	private Graph graph = new Graph();
	private Geometry geo;
	private NodeVisual nodeVisual;
	private EdgeVisual edgeVisual;
	private boolean dragging = false;
	private Point2D.Float graphOrigin = new Point2D.Float(0, 0);
	private GraphMouseHandler mouseHandler;

	private GeometryListener sizeListener = (geo, e) -> SwingUtilities.invokeLater(() -> updateSize());

	protected final static Stroke clippingDebugStroke = new BasicStroke(1f);

	/**
	 * Counts paint calls. Can be used to show some fps indicator.
	 */
	public int paintCount = 0;


	public GraphPanel()
	{
		setLayout(null);
		mouseHandler = new GraphMouseHandler(this);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
		setNodeVisual(null);
		setEdgeVisual(null);
	}

	public void setNodeVisual(NodeVisual v)
	{
		Node root = graph.getRoot();
		if (root != null && this.geo != null)
			this.geo.removeDependency(sizeListener, root);

		if (v == null)
		{
			v = new NodeLabelVisual(new TreeLayout(new TreeRectangleGeometry()), new VisualSettings());
		}

		this.geo = v.getGeometry();
		if (root != null)
			this.geo.addDependency(sizeListener, root);

		this.geo.clear();
		this.graphOrigin.x = 0;
		this.graphOrigin.y = 0;
		this.nodeVisual = v;
	}

	public NodeVisual getNodeVisual()
	{
		return nodeVisual;
	}

	public void setEdgeVisual(EdgeVisual v)
	{
		edgeVisual = v;
	}

	@Override
	public void updateUI()
	{
		super.updateUI();

		if (geo != null)
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
		VisualSettings settings = nodeVisual.getVisualSettings();
		final float scale = settings.scale;
		if (root != null)
		{
			Context ctx = new Context(g);
			ctx.debug_ = settings.debug;

			ctx.currentBackground_ = settings.background == null ? getBackground() : settings.background;

			try
			{
				final Graphics2D g2 = ctx.g2D_;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.scale(scale, scale);
				g2.translate(graphOrigin.x, graphOrigin.y);

				Rectangle clipping = g2.getClipBounds();
				g2.setPaint( ctx.currentBackground_ );
				g2.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);

				if (settings.debug)
				{
					g2.setColor(Color.BLUE);
					g2.setStroke(clippingDebugStroke);
					g2.drawRect(clipping.x, clipping.y, clipping.width - 1, clipping.height - 1);
				}
				// clipping = new Rectangle(clipping.x - 1, clipping.y - 1, clipping.width + 2, clipping.height + 2);


				g2.setPaint( settings.edge.color );
				ctx.currentColor_ = settings.edge.color;
				paintTreeEdges(ctx, clipping, root);

				g2.setPaint( settings.node.border );
				ctx.currentColor_ = settings.node.border;
				paintTreeNodes(ctx, clipping, root);
				if (settings.edge.decorate)
				{
					g2.setPaint( settings.edge.color );
					ctx.currentColor_ = settings.edge.color;
					paintTreeEdgeEndPoints(ctx, clipping, root);
				}
			}
			finally
			{
				ctx.dispose();
				++paintCount;
			}
		}
		else
		{
			Rectangle clipping = g.getClipBounds();
			g.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);
		}
	}

	public void doLayoutGraph()
	{
		Node root = graph.getRoot();
		if (root != null)
		{
			geo.beginUpdate();
			updateGeometry((Graphics2D) getGraphics(), root);
			nodeVisual.getLayout()
					  .placeChildren(root);
			geo.endUpdate();
			updateSize();
		}
	}

	protected void updateGeometry(Graphics2D g, Node root)
	{
		nodeVisual.updateGeometry(g, root);
		for (Iterator<Node> it = root.children(); it.hasNext(); )
		{
			Node c = it.next();
			updateGeometry(g, c);
		}
	}

	protected void paintTreeNodes(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual.getVisualBounds(n)
					  .intersects(area))
			nodeVisual.paint(ctx, n);
		if (nodeVisual.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.source == n)
				{
					if (!e.cylic && e.target != n)
					{
						paintTreeNodes(ctx, area, e.target);
					}
				}
			}
		}
	}

	protected void paintTreeEdges(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.source == n)
				{
					edgeVisual.paint(ctx, e);
					if (!e.cylic && e.target != n)
					{
						paintTreeEdges(ctx, area, e.target);
					}
				}
			}
		}
	}

	protected void paintTreeEdgeEndPoints(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.source == n)
				{
					edgeVisual.paintEndPoint(ctx, e);
					if (!e.cylic && e.target != n)
					{
						paintTreeEdgeEndPoints(ctx, area, e.target);
					}
				}
			}
		}
	}

	/**
	 *
	 * @param p Point in Graph-coordinates
	 */
	protected Node getNodeAt(Node root, Point2D p)
	{
		if (geo.getBounds(root)
			   .contains(p))
		{
			return root;
		}
		else if (nodeVisual.isExpanded(root))
		{
			for (Edge e : root.edges)
			{
				if (e.target != root)
				{
					Node n = getNodeAt(e.target, p);
					if (n != null)
						return n;
				}
			}
		}
		return null;
	}

	public Node getNodeAt(Point2D p)
	{
		Node root = graph.getRoot();
		if (root == null)
			return null;
		else
		{
			return getNodeAt(root, screenToGraphCoordinates(p));
		}
	}

	public Node getNodeAt(int x, int y)
	{
		return getNodeAt(new Point2D.Float(x, y));
	}

	public Point2D.Float getNodeLocation(Node node)
	{
		Rectangle2D r = nodeVisual.getGeometry()
								  .getBounds(node);
		if (r != null)
		{
			final float scale = nodeVisual.getVisualSettings().scale;
			Point2D.Float pt = new Point2D.Float();
			pt.x = (float)(r.getX() + graphOrigin.x) * scale;
			pt.y = (float)(r.getY() + graphOrigin.y) * scale;
			return pt;
		}
		return null;
	}


	public void moveNode(Node node, double dx, double dy, boolean moveTree)
	{
		final float scale = nodeVisual.getVisualSettings().scale;
		if ( moveTree )
			geo.moveTree(graph, node, dx / scale, dy / scale);
		else
			geo.moveNode(graph, node, dx / scale, dy / scale, nodeVisual.isExpanded(node));
		repaintIfNeeded();
		if (!dragging)
		{
			updateSize();
		}
	}

	public void moveOrigin(int dx, int dy)
	{
		final float scale = nodeVisual.getVisualSettings().scale;
		graphOrigin.x += (int) (dx / scale);
		graphOrigin.y += (int) (dy / scale);
		if (!dragging)
		{
			updateSize();
		}
		repaint();
	}


	public Graph getGraph()
	{
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

	protected void updateSize()
	{

		Node root = graph.getRoot();
		if (root != null)
		{
			Rectangle2D r = geo.getGraphBounds(root);
			final float scale = nodeVisual.getVisualSettings().scale;

			Dimension d = new Dimension((int) (scale * (r.getX() + graphOrigin.x + r.getWidth() + 5)),
					(int) (scale * (r.getY() + graphOrigin.y + r.getHeight() + 5)));
			Dimension s = getSize();
			if ((d.width > s.width || d.height > s.height) || (d.width < (s.width - 20) || d.height < (s.height - 20)))
			{
				setPreferredSize(d);
				revalidate();
			}
		}
		else
		{
			graphOrigin = new Point2D.Float(0, 0);
			setPreferredSize(new Dimension(0, 0));
			revalidate();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension d;
		if (isPreferredSizeSet())
			d = super.getPreferredSize();
		else
		{
			Node root = graph.getRoot();
			if (root != null)
			{
				Rectangle2D r = geo.getGraphBounds(root);
				if (r == null)
				{
					doLayoutGraph();
				}
				else
					updateSize();
				d = super.getPreferredSize();
			}
			else
				d = getMinimumSize();
		}
		return d;
	}

	public Point2D.Float screenToGraphCoordinates( Point2D p)
	{
		final float scale = nodeVisual.getVisualSettings().scale;
		Point2D.Float pf = new Point2D.Float(
				(float)(p.getX()/scale - graphOrigin.x),
				(float)(p.getY()/scale - graphOrigin.y)
		);
		return pf;
	}

	public void repaintIfNeeded()
	{
		final Rectangle2D da = geo.getDirtyArea();
		if (da != null && !da.isEmpty())
		{
			final float scale = nodeVisual.getVisualSettings().scale;

			Rectangle repaintArea = new Rectangle((int) da.getX(), (int) da.getY(), (int) da.getWidth(), (int) da.getHeight());
			repaintArea.grow(2, 2);
			repaintArea.translate((int) graphOrigin.x, (int) graphOrigin.y);

			repaintArea.x *= scale;
			repaintArea.y *= scale;
			repaintArea.width *= scale;
			repaintArea.height *= scale;

			repaint(repaintArea);

			geo.resetDirtyArea();
		}
	}

}
