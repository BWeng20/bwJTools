package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.Log;
import com.bw.jtools.graph.Edge;
import com.bw.jtools.graph.Node;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.graph.Geometry;
import com.bw.jtools.ui.graph.Layout;
import com.bw.jtools.ui.graph.Visual;
import com.bw.jtools.ui.graph.VisualState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Shows only raw text.
 */
public class SimpleVisual implements Visual
{
	protected int margin_y = 5;
	protected int margin_x = 5;
	protected int margin_y2 = 10;
	protected int margin_x2 = 10;
	protected Geometry geo;
	protected Layout layout;

	protected Map<Integer, VisualState> visualStates = new HashMap<>();

	protected Node focusedNode;
	protected Point focusedNodePressedAt;

	protected BufferedImage expandImage;
	protected BufferedImage collapseImage;

	protected static BufferedImage defaultExpandImage;
	protected static BufferedImage defaultCollapseImage;
	protected static int defaultExpandWidth = 10;

	protected Stroke edgeStroke;
	protected Stroke borderStroke;
	protected Stroke focusStroke;

	protected final static Stroke debugStroke;

	protected static boolean debug = false;

	@Override
	public VisualState getVisualState( Node node ) {
		VisualState s = visualStates.get(node.id);
		if ( s == null ) {
			s = new VisualState();
			s.expandable = node.children().hasNext();
			s.expanded = false;
			visualStates.put(node.id,s);
		}
		return s;
	}

	@Override
	public Rectangle getVisualBounds( Node n )
	{
		Rectangle r =geo.getBounds(n);
		if ( r != null )
			r = new Rectangle(r);
		else
			r = new Rectangle();
		return r;
	}


	@Override
	public void setDebug( boolean debug )
	{
		SimpleVisual.debug = debug;
	}

	@Override
	public boolean isDebug( )
	{
		return debug;
	}

	static
	{
		try
		{
			defaultExpandImage = IconCache.getImage(SimpleVisual.class, "icons/expand.png");
			defaultCollapseImage = IconCache.getImage(SimpleVisual.class, "icons/collapse.png");
		} catch (IOException io)
		{
			Log.error("Failed to load images", io);
		}
		if (defaultExpandImage == null)
		{
			BufferedImage expandImage = IconCache.createImage(defaultExpandWidth, defaultExpandWidth, true);
			Graphics2D g = expandImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillOval(1, 1, defaultExpandWidth - 2, defaultExpandWidth - 2);
			g.setColor(Color.BLACK);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawOval(1, 1, defaultExpandWidth - 2, defaultExpandWidth - 2);

			int centerx = (int) (0.5 + defaultExpandWidth / 2.0);
			int centery = (int) (0.5 + defaultExpandWidth / 2.0);
			int gab = 1 + (int) (0.5 + defaultExpandWidth / 5.0);

			g.drawLine(centerx, gab, centerx, defaultExpandWidth - gab);
			g.drawLine(gab, centery, defaultExpandWidth - gab, centery);
			g.dispose();

			defaultExpandImage = expandImage;
		}

		if (defaultCollapseImage == null)
		{
			BufferedImage collapseImage = IconCache.createImage(defaultExpandWidth, defaultExpandWidth, true);
			Graphics2D g = collapseImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillOval(1, 1, defaultExpandWidth - 2, defaultExpandWidth - 2);
			g.setColor(Color.BLACK);
			g.drawOval(1, 1, defaultExpandWidth - 2, defaultExpandWidth - 2);

			int centery = (int) (0.5 + defaultExpandWidth / 2.0);
			int gab = 1 + (int) (0.5 + defaultExpandWidth / 5.0);

			g.drawLine(gab, centery, defaultExpandWidth - gab, centery);
			g.dispose();

			SimpleVisual.defaultCollapseImage = collapseImage;
		}

		debugStroke = new BasicStroke(1);

	}

	public SimpleVisual(Layout layout )
	{
		this.geo = layout.getGeometry();
		this.layout = layout;
		this.expandImage = defaultExpandImage;
		this.collapseImage = defaultCollapseImage;

		this.borderStroke = new BasicStroke(1.5f);
		this.edgeStroke = new BasicStroke(2f);
		final float dashes[] = {5};
		this.focusStroke = new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashes, 0.0f);
	}

	@Override
	public int getHorizontalMargin() {
		return margin_x;
	}

	@Override
	public int getVerticalMargin() {
		return margin_y;
	}

	@Override
	public void setHorizontalMargin( int margin )
	{
		margin_x = margin;
		margin_x2 = margin<<1;
	}

	@Override
	public void setVerticalMargin(int margin )
	{
		margin_y = margin;
		margin_y2 = margin<<1;
	}

	@Override
	public void expand(Node node, boolean expand)
	{
		VisualState state = getVisualState(node);
		if ( expand != state.expanded )
		{
			geo.beginUpdate();
			geo.dirty( getVisualBounds(node) );
			state.expanded = expand;
			for (Iterator<Node> it = node.children(); it.hasNext(); )
			{
				geo.setVisibility( it.next(), expand );
			}
			if ( expand )
				layout.placeChildren(node);
			geo.endUpdate();
		}
	}

	@Override
	public void setExpandable(Node node, boolean expandable)
	{
		VisualState state = getVisualState(node);
		if ( expandable != state.expandable )
		{
			state.expandable = expandable;
			geo.dirty(geo.getBounds(node));
		}
	}

	public void paintBorder(Graphics2D g, Node node, Rectangle bounds)
	{
		boolean focused = ( focusedNode == node );

		g.setColor(Color.BLACK);
		g.setStroke( borderStroke );
		g.drawRect(bounds.x+margin_x, bounds.y+margin_y, bounds.width-margin_x2, bounds.height-margin_y2);
		if ( focused ) {
			g.setColor(Color.GRAY);
			g.setStroke( focusStroke);
			g.drawRect(bounds.x+margin_x/2, bounds.y+margin_y/2, bounds.width-margin_x+1, bounds.height-margin_y+1);
		}
		paintExpandButton( g, node, bounds, focused );
	}

	public void paintExpandButton(Graphics g, Node node, Rectangle bounds, boolean focused)
	{
		VisualState state = getVisualState(node);
		if ( state.expandable )
		{
			if (state.expanded)
				g.drawImage(collapseImage, bounds.x + bounds.width - collapseImage.getWidth(), bounds.y + (bounds.height - collapseImage.getHeight()) / 2, null);
			else
				g.drawImage(expandImage, bounds.x + bounds.width - expandImage.getWidth(), bounds.y + (bounds.height - expandImage.getHeight()) / 2, null);
		}
	}

	@Override
	public void paint(Graphics2D g, Node node)
	{
		Rectangle r = (Rectangle) geo.getShape(node);

		if ( debug )
		{
			Rectangle tr = geo.getTreeArea(node);
			if ( tr != null )
			{
				g.setColor(Color.RED);
				g.setStroke(debugStroke);
				g.drawRect(tr.x, tr.y, tr.width, tr.height);
			}
		}

		paintBorder( g, node, r );

		final FontMetrics m = g.getFontMetrics();
		String text = ((TextData) node.data).text;

		final int lineHeight = m.getHeight();
		final int x = r.x + margin_x2;
		int y = r.y + margin_y2 + lineHeight - m.getDescent();
		int i1 = 0;
		final int n = text.length();
		do
		{
			int i2 = text.indexOf('\n', i1);
			if (i2 < 0) i2 = n;
			g.drawString(text.substring(i1, i2), x, y);
			y += lineHeight;
			i1 = i2 + 1;
		} while (i1 >= 0 && i1 < n);
	}

	@Override
	public void paint(Graphics2D g, Edge edge) {
		Rectangle r1 = geo.getShape( edge.source).getBounds();
		Rectangle r2 = geo.getShape( edge.target).getBounds();
		g.setColor(Color.GRAY);
		g.setStroke( edgeStroke );
		g.drawLine(r1.x+r1.width-margin_x, r1.y+(r1.height/2), r2.x+margin_x, r2.y+(r2.height/2));
	}

	@Override
	public void updateGeometry( Graphics2D g, Node node ) {

		geo.beginUpdate();
		final FontMetrics m = g.getFontMetrics( );
		String text = ((TextData)node.data).text;

		Rectangle r = new Rectangle();
		int lineHeight = m.getHeight();
		int i1 = 0;
		final int n = text.length();
		do {
			int i2 = text.indexOf('\n', i1);
			if ( i2 < 0 ) i2 = n;
			Rectangle rt = m.getStringBounds( text, i1, i2, g ).getBounds();
			if ( i2 < 0 ) i2 = n;
			int w = rt.width;
			r.height += lineHeight;
			if ( w > r.width ) r.width = w;
			i1 = i2 + 1;
		} while ( i1 >= 0 && i1 < n );

		r.height += (4*margin_y);
		r.width  += (4*margin_x);

		geo.setShape(node,r);
		updateVisibility(node);

		geo.endUpdate();

	}

	/**
	 * Updates visibility according to parents expand state.
	 */
	protected void updateVisibility(Node node)
	{
		Iterator<Node> it = node.parents();
		boolean visible = !it.hasNext();
		while ( it.hasNext() )
		{
			if ( isExpanded( it.next() ) )
			{
				visible = true;
				break;
			}
		}
		geo.setVisibility(node,visible);

	}

	@Override
	public Geometry getGeometry()
	{
		return geo;
	}

	@Override
	public Layout getLayout()
	{
		return layout;
	}

	public void click( Node node, Point p )
	{
		if ( isExpandable(node))
		{
			Rectangle r = geo.getBounds(node);
			if (r != null) {
				Rectangle expandBox = new Rectangle( r.width-expandImage.getWidth(), (r.height - expandImage.getHeight()) / 2, expandImage.getWidth(), expandImage.getHeight());
				if ( expandBox.contains( p ) ) {
					expand( node, !isExpanded(node ));
				}
			}
		}

	}

	public void setFocusedNode(Node node, Point pressedAt)
	{
		if (focusedNode != node)
		{
			if ( focusedNode != null )
			{
				final Rectangle fr = geo.getBounds(focusedNode);
				if (fr != null) geo.dirty(fr);
			}
			if ( node != null )
			{
				final Rectangle r = geo.getBounds(node);
				if (r != null) geo.dirty(r);
			}
			focusedNode = node;
		}
		if ( pressedAt != null )
			focusedNodePressedAt = new Point(pressedAt);
		else
			focusedNodePressedAt = null;

	}

	@Override
	public void pressed(Node node, Point graphPoint)
	{
		setFocusedNode( node, graphPoint);
	}

	@Override
	public void released()
	{
		focusedNodePressedAt = null;
		if ( focusedNode != null )
		{
			final Rectangle fr = geo.getBounds(focusedNode);
			if (fr != null) geo.dirty(fr);
		}
	}


}
