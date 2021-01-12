package com.bw.jtools.ui.graph;

import com.bw.jtools.ui.graph.Graph;
import com.bw.jtools.ui.graph.GraphPanel;
import com.bw.jtools.ui.graph.Visual;
import com.bw.jtools.ui.properties.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.text.NumberFormat;

public class GraphOptionDialog extends JDialog
{
	PropertyTable table = new PropertyTable();
	GraphPanel graphPanel;

	public GraphOptionDialog(GraphPanel graphPanel)
	{
		super(SwingUtilities.getWindowAncestor(graphPanel), ModalityType.MODELESS);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setLayout(new BorderLayout());
		add(table, BorderLayout.CENTER);
		this.graphPanel = graphPanel;

		table.getModel().addTableModelListener(new TableModelListener()
		{
			NumberFormat nf = NumberFormat.getInstance();

			@Override
			public void tableChanged(TableModelEvent ev)
			{

			}
		});

	}

	protected void addProperty(PropertyGroup group, PropertyValue value, PropertyChangeListener pcl) {
		value.addPropertyChangeListener(pcl);
		value.nullable_ = false;
		group.addProperty(value);
	}

	protected void addProperty(PropertyGroup group, String displayName, int value, PropertyChangeListener pcl) {
		addProperty( group, new PropertyNumberValue(displayName, value), pcl );
	}

	protected int getInt(PropertyValue value, int defaultValue ) {
		if ( value instanceof PropertyNumberValue) {
			Number nb = ((PropertyNumberValue)value).getValue();
			if ( nb != null )
				return nb.intValue();
		}
		return defaultValue;
	}

	protected void addProperty(PropertyGroup group, String displayName, boolean value, PropertyChangeListener pcl) {
		addProperty( group, new PropertyBooleanValue(displayName, value), pcl );
	}

	protected boolean getBoolean(PropertyValue value, boolean defaultValue ) {
		if ( value instanceof PropertyBooleanValue) {
			Boolean nb = ((PropertyBooleanValue)value).getValue();
			if ( nb != null )
				return nb;
		}
		return defaultValue;
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();

		int h = table.getTableModel().getRowCount() * table.getRowHeight();
		int w = table.getFontMetrics( table.getFont() ).charWidth('A')*50;

		return new Dimension( Math.max( w, d.width), Math.max(h,d.height));
	}

	public void init() {

		Graph g = graphPanel.getGraph();
		Visual v = graphPanel.getVisual();

		DefaultTreeModel model = table.getTreeModel();
		PropertyGroup root = new PropertyGroup("Root");
		PropertyGroup p = new PropertyGroup("Visual Settings");
		root.add(p);

		addProperty( p, "Vertical Margin", v.getVerticalMargin(), value -> { v.setVerticalMargin( getInt( value, v.getVerticalMargin() ) ); graphPanel.repaint();});
		addProperty( p,  "Horizontal Margin", v.getHorizontalMargin(), value -> { v.setHorizontalMargin( getInt( value, v.getHorizontalMargin() ) ); graphPanel.repaint();});
		addProperty( p,  "Debug", v.isDebug(), value -> { v.setDebug( getBoolean( value, v.isDebug() ) ); graphPanel.repaint();});

		model.setRoot(root);
		table.expandAll();
		// table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

	}
}