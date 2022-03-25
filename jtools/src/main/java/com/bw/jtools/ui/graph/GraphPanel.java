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
	private Graph graph_ = new Graph();
	private Geometry geo_;
	private NodeVisual nodeVisual_;
	private EdgeVisual edgeVisual_;
	private boolean dragging_ = false;
	private Point2D.Float graphOrigin_ = new Point2D.Float(0, 0);
	private GraphMouseHandler mouseHandler_;

	private GeometryListener sizeListener_ = (geo, e) -> SwingUtilities.invokeLater(() -> updateSize());

	/**
	 * Counts paint calls. Can be used to show some fps indicator.
	 */
	public int paintCount_ = 0;


	public GraphPanel()
	{
		setLayout(null);
		mouseHandler_ = new GraphMouseHandler(this);
		addMouseListener(mouseHandler_);
		addMouseMotionListener(mouseHandler_);
		setNodeVisual(null);
		setEdgeVisual(null);
	}

	/**
	 * Sets the node visual to use.
	 */
	public void setNodeVisual(NodeVisual v)
	{
		Node root = graph_.getRoot();
		if (root != null && this.geo_ != null)
			this.geo_.removeDependency(sizeListener_, root);

		if (v == null)
		{
			v = new NodeLabelVisual(new TreeLayout(new TreeRectangleGeometry()), new VisualSettings());
		}

		this.geo_ = v.getGeometry();
		if (root != null)
			this.geo_.addDependency(sizeListener_, root);

		this.geo_.clear();
		this.graphOrigin_.x = 0;
		this.graphOrigin_.y = 0;
		this.nodeVisual_ = v;
	}

	public NodeVisual getNodeVisual()
	{
		return nodeVisual_;
	}

	public void setEdgeVisual(EdgeVisual v)
	{
		edgeVisual_ = v;
	}

	@Override
	public void updateUI()
	{
		super.updateUI();

		if (geo_ != null)
		{
			geo_.clear();
			graphOrigin_.x = 0;
			graphOrigin_.y = 0;
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Node root = graph_.getRoot();
		VisualSettings settings = nodeVisual_.getVisualSettings();
		final float scale = settings.scale_;
		if (root != null)
		{
			Context ctx = new Context(g);
			ctx.debug_ = settings.debug_;
			if ( ctx.debug_ )
			{
				ctx.debugPaint_ = settings.debugPaint_;
				ctx.debugStroke_= settings.debugStoke_;
			}

			ctx.currentBackground_ = settings.background_ == null ? getBackground() : settings.background_;

			try
			{
				final Graphics2D g2 = ctx.g2D_;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.scale(scale, scale);
				g2.translate(graphOrigin_.x, graphOrigin_.y);

				Rectangle clipping = g2.getClipBounds();
				g2.setPaint( ctx.currentBackground_ );
				g2.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);

				if (settings.debug_)
				{
					g2.setColor(Color.BLUE);
					g2.setStroke(settings.debugStoke_);
					g2.drawRect(clipping.x, clipping.y, clipping.width - 1, clipping.height - 1);
				}

				g2.setPaint( settings.edge_.color );
				ctx.currentColor_ = settings.edge_.color;
				paintTreeEdges(ctx, clipping, root);

				g2.setPaint( settings.node_.border );
				ctx.currentColor_ = settings.node_.border;
				paintTreeNodes(ctx, clipping, root);
				if (settings.edge_.decorate)
				{
					g2.setPaint( settings.edge_.color );
					ctx.currentColor_ = settings.edge_.color;
					paintTreeEdgeEndPoints(ctx, clipping, root);
				}
			}
			finally
			{
				ctx.dispose();
				++paintCount_;
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
		Node root = graph_.getRoot();
		if (root != null)
		{
			geo_.beginUpdate();
			updateGeometry((Graphics2D) getGraphics(), root);
			nodeVisual_.getLayout()
					   .placeChildren(root);
			geo_.endUpdate();
			updateSize();
		}
	}

	protected void updateGeometry(Graphics2D g, Node root)
	{
		nodeVisual_.updateGeometry(g, root);
		for (Iterator<Node> it = root.children(); it.hasNext(); )
		{
			Node c = it.next();
			updateGeometry(g, c);
		}
	}

	protected void paintTreeNodes(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual_.getVisualBounds(n)
					   .intersects(area))
			nodeVisual_.paint(ctx, n);
		if (nodeVisual_.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.getSource() == n)
				{
					final Node t = e.getTarget();
					if (!e.isCyclic() && t != n)
					{
						paintTreeNodes(ctx, area, t);
					}
				}
			}
		}
	}

	protected void paintTreeEdges(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual_.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.getSource() == n)
				{
					final Node t = e.getTarget();
					edgeVisual_.paint(ctx, e);
					if (!e.isCyclic() && t != n)
					{
						paintTreeEdges(ctx, area, t);
					}
				}
			}
		}
	}

	protected void paintTreeEdgeEndPoints(Context ctx, Rectangle area, Node n)
	{
		if (nodeVisual_.isExpanded(n))
		{
			for (Edge e : n.edges)
			{
				if (e.getSource() == n)
				{
					final Node t = e.getTarget();
					edgeVisual_.paintEndPoint(ctx, e);
					if (!e.isCyclic() && t != n)
					{
						paintTreeEdgeEndPoints(ctx, area, t);
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
		if (geo_.getBounds(root)
				.contains(p))
		{
			return root;
		}
		else if (nodeVisual_.isExpanded(root))
		{
			for (Edge e : root.edges)
			{
				final Node t = e.getTarget();
				if (t != root)
				{
					Node n = getNodeAt(t, p);
					if (n != null)
						return n;
				}
			}
		}
		return null;
	}

	public Node getNodeAt(Point2D p)
	{
		Node root = graph_.getRoot();
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
		Rectangle2D r = nodeVisual_.getGeometry()
								   .getBounds(node);
		if (r != null)
		{
			final float scale = nodeVisual_.getVisualSettings().scale_;
			Point2D.Float pt = new Point2D.Float();
			pt.x = (float)(r.getX() + graphOrigin_.x) * scale;
			pt.y = (float)(r.getY() + graphOrigin_.y) * scale;
			return pt;
		}
		return null;
	}


	public void moveNode(Node node, double dx, double dy, boolean moveTree)
	{
		final float scale = nodeVisual_.getVisualSettings().scale_;
		if ( moveTree )
			geo_.moveTree(graph_, node, dx / scale, dy / scale);
		else
			geo_.moveNode(graph_, node, dx / scale, dy / scale, nodeVisual_.isExpanded(node));
		repaintIfNeeded();
		if (!dragging_)
		{
			updateSize();
		}
	}

	public void moveOrigin(int dx, int dy)
	{
		final float scale = nodeVisual_.getVisualSettings().scale_;
		graphOrigin_.x += (int) (dx / scale);
		graphOrigin_.y += (int) (dy / scale);
		if (!dragging_)
		{
			updateSize();
		}
		repaint();
	}


	public Graph getGraph()
	{
		return graph_;
	}

	public void startNodeDrag()
	{
		dragging_ = true;
	}

	public void endNodeDrag()
	{
		dragging_ = false;
		updateSize();
	}

	protected void updateSize()
	{

		Node root = graph_.getRoot();
		if (root != null)
		{
			Rectangle2D r = geo_.getGraphBounds(root);
			final float scale = nodeVisual_.getVisualSettings().scale_;

			Dimension d = new Dimension((int) (scale * (r.getX() + graphOrigin_.x + r.getWidth() + 5)),
					(int) (scale * (r.getY() + graphOrigin_.y + r.getHeight() + 5)));
			Dimension s = getSize();
			if ((d.width > s.width || d.height > s.height) || (d.width < (s.width - 20) || d.height < (s.height - 20)))
			{
				setPreferredSize(d);
				revalidate();
			}
		}
		else
		{
			graphOrigin_ = new Point2D.Float(0, 0);
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
			Node root = graph_.getRoot();
			if (root != null)
			{
				Rectangle2D r = geo_.getGraphBounds(root);
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
		final float scale = nodeVisual_.getVisualSettings().scale_;
		Point2D.Float pf = new Point2D.Float(
				(float)(p.getX()/scale - graphOrigin_.x),
				(float)(p.getY()/scale - graphOrigin_.y)
		);
		return pf;
	}

	public void repaintIfNeeded()
	{
		final Rectangle2D da = geo_.getDirtyArea();
		if (da != null && !da.isEmpty())
		{
			final float scale = nodeVisual_.getVisualSettings().scale_;

			Rectangle repaintArea = new Rectangle((int) da.getX(), (int) da.getY(), (int) da.getWidth(), (int) da.getHeight());
			repaintArea.grow(2, 2);
			repaintArea.translate((int) graphOrigin_.x, (int) graphOrigin_.y);

			repaintArea.x *= scale;
			repaintArea.y *= scale;
			repaintArea.width *= scale;
			repaintArea.height *= scale;

			repaint(repaintArea);

			geo_.resetDirtyArea();
		}
	}

}
