package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyChangeListener;
import com.bw.jtools.properties.PropertyColorValue;
import com.bw.jtools.properties.PropertyEnumValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class GraphOptionDialog extends JDialog
{
	PropertyTable table = new PropertyTable();
	GraphPanel graphPanel;

	List<PropertyChangeListener> propertyChangeListener = new ArrayList<>();

	public GraphOptionDialog(GraphPanel graphPanel)
	{
		super(SwingUtilities.getWindowAncestor(graphPanel), ModalityType.MODELESS);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setLayout(new BorderLayout());
		add(table, BorderLayout.CENTER);
		this.graphPanel = graphPanel;

	}

	protected VisualSettings getVisualSettings()
	{
		return graphPanel.getNodeVisual()
						 .getVisualSettings();
	}

	protected void addProperty(PropertyGroup group, PropertyValue value, PropertyChangeListener pcl)
	{
		// Add the lamdas via strong reference, since they are deleted otherwise.
		propertyChangeListener.add(pcl);
		value.addPropertyChangeListener(pcl);
		value.nullable_ = false;
		group.addProperty(value);
	}

	protected int getInt(PropertyValue value, int defaultValue)
	{
		if (value instanceof PropertyNumberValue)
		{
			Number nb = ((PropertyNumberValue) value).getValue();
			if (nb != null)
				return nb.intValue();
		}
		return defaultValue;
	}

	protected boolean getBoolean(PropertyValue value, boolean defaultValue)
	{
		if (value instanceof PropertyBooleanValue)
		{
			Boolean nb = ((PropertyBooleanValue) value).getValue();
			if (nb != null)
				return nb;
		}
		return defaultValue;
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();

		int h = table.getTableModel()
					 .getRowCount() * table.getRowHeight();
		int w = table.getFontMetrics(table.getFont())
					 .charWidth('A') * 50;

		return new Dimension(Math.max(w, d.width), Math.max(h, d.height));
	}

	public void init()
	{
		Graph g = graphPanel.getGraph();
		NodeVisual v = graphPanel.getNodeVisual();

		DefaultTreeModel model = table.getTreeModel();

		PropertyGroup p = new PropertyGroup("Visual Settings");

		addProperty(p, new PropertyNumberValue("Zoom", v.getVisualSettings().scale), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Number n = ((PropertyNumberValue)value).getValue();
			nv.getVisualSettings().scale = (n == null) ? 1 : n.floatValue();
			if ( nv.getVisualSettings().scale <= 0)
			{
				nv.getVisualSettings().scale = 1;
				((PropertyNumberValue) value).setValue(nv.getVisualSettings().scale);
			}
			graphPanel.repaint();
		});


		addProperty(p, new PropertyColorValue("Edge", v.getVisualSettings().edge.color), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Color c = ((PropertyColorValue) value).getValue();
			nv.getVisualSettings().edge.color = (c == null) ? Color.BLACK : c;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyEnumValue<EdgeMode>("Edge Type", v.getVisualSettings().edge.mode), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			EdgeMode e = ((PropertyEnumValue<EdgeMode>) value).getValue();
			nv.getVisualSettings().edge.mode = (e == null) ? EdgeMode.STRAIGHT : e;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyBooleanValue("Edge Decorate", v.getVisualSettings().edge.decorate), value ->
		{
			getVisualSettings().edge.decorate = getBoolean(value, getVisualSettings().edge.decorate);
			graphPanel.repaint();
		});

		addProperty(p, new PropertyBooleanValue("Node opaque", v.getVisualSettings().node.opaque), value ->
		{
			getVisualSettings().node.opaque = getBoolean(value, getVisualSettings().node.opaque);
			graphPanel.repaint();
		});

		addProperty(p, new PropertyColorValue("Node Border", v.getVisualSettings().node.border), value ->
		{
			Color c = ((PropertyColorValue) value).getValue();
			getVisualSettings().node.border = (c == null) ? Color.LIGHT_GRAY : c;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyColorValue("Node Background", v.getVisualSettings().node.background), value ->
		{
			Color c = ((PropertyColorValue) value).getValue();
			getVisualSettings().node.background = (c == null) ? Color.LIGHT_GRAY : c;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyNumberValue("Vertical Margin", v.getVerticalMargin()), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			nv.setVerticalMargin(getInt(value, nv.getVerticalMargin()));
			graphPanel.repaint();
		});

		addProperty(p, new PropertyNumberValue("Horizontal Margin", v.getVerticalMargin()), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			nv.setHorizontalMargin(getInt(value, nv.getHorizontalMargin()));
			graphPanel.repaint();
		});

		addProperty(p, new PropertyBooleanValue("Debug", v.getVisualSettings().debug), value ->
		{
			getVisualSettings().debug = getBoolean(value, getVisualSettings().debug);
			graphPanel.repaint();
		});

		PropertyGroupNode root = new PropertyGroupNode(null);
		root.add(new PropertyGroupNode(p));

		model.setRoot(root);
		table.expandAll();
		// table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

	}
}