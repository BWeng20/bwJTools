package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.ui.graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultVisual extends SimpleVisual
{
	Map<Integer, JNodeRenderer> labels = new HashMap<>();

	public DefaultVisual( Geometry geo ) {
		super(geo);
	}

	@Override
	public void paint(Graphics2D g, Node node)
	{
		Rectangle r = geo.getShape(node).getBounds();

		if ( debug )
		{
			g.setColor(Color.RED);
			g.setStroke(debugStroke);
			Rectangle tr = geo.getTreeArea(node);
			g.drawRect(tr.x, tr.y, tr.width, tr.height);
		}

		paintBorder( g, node, r );

		JLabel l = labels.get(node.id);
		if ( l != null) {
			String text = ((TextData)node.data).text;
			if ( !text.equals(l.getText()) )
			{
				l.setText(text);
			}
			l.setForeground( Color.BLACK );
			r.x += margin_x2;
			r.y += margin_y2;
			r.width -= margin_x<<2;
			r.height -= margin_y<<2;
			l.setBounds(r);

			Graphics cg = g.create(r.x, r.y, r.width, r.height);
			try {
				l.paint(cg);
			}
			finally {
				cg.dispose();
			}


		}
	}

	@Override
	public void updateGeometry( Graphics2D g, Node node ) {

		geo.beginUpdate();

		String text = ((TextData)node.data).text;

		JNodeRenderer l = labels.get(node.id);
		if ( l == null){
			l = new JNodeRenderer();
			labels.put(node.id, l);
		}
		l.setText(text);
		l.setFont(g.getFont());
		Dimension ps = l.getPreferredSize();
		l.setSize( ps );
		geo.setShape(node, new Rectangle(0,0,ps.width+(margin_x<<2), ps.height+(margin_y<<2)));
		updateVisibility(node);
		geo.endUpdate();
	}


}
