package com.bw.jtools.ui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class GraphicContext
{
	public Graphics2D g2D_;
	public Paint currentColor_;
	public Paint currentBackground_;
	private final boolean newContext_;

	public boolean debug_ = false;
	/**
	 * Stroke for debug lines.
	 */
	public static Stroke debugStroke_ = new BasicStroke(0.5f);
	/**
	 * Paint for debug lines.
	 */
	public static Paint debugPaint_ = Color.RED;

	public static Object renderingHint_Antialias_ = RenderingHints.VALUE_ANTIALIAS_ON;

	/**
	 * Placeholder for "none" color.
	 */
	public static final Color NONE = new Color(0, 0, 0, 0);


	public GraphicContext(GraphicContext ctx)
	{
		this(ctx, true);
	}

	public GraphicContext(GraphicContext ctx, boolean createNewContext)
	{
		newContext_ = createNewContext;
		if (createNewContext)
		{
			this.g2D_ = (Graphics2D) ctx.g2D_.create();
		}
		else
			this.g2D_ = ctx.g2D_;
		currentColor_ = ctx.currentColor_;
		currentBackground_ = ctx.currentBackground_;
		debug_ = ctx.debug_;
	}

	public GraphicContext(Graphics g2D)
	{
		this(g2D, true);
	}

	public GraphicContext(Graphics g2D, boolean createNewContext)
	{
		this.newContext_ = createNewContext;
		if (createNewContext)
		{
			this.g2D_ = (Graphics2D) g2D.create();
		}
		else
		{
			this.g2D_ = (Graphics2D) g2D;
		}
		this.currentColor_ = this.g2D_.getPaint();
	}

	/**
	 * Sets rendering hints.
	 */
	public static void initGraphics(Graphics2D g2d)
	{
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, GraphicContext.renderingHint_Antialias_);
	}

	public void dispose()
	{
		if (newContext_)
		{
			g2D_.dispose();
			g2D_ = null;
		}
	}


}
