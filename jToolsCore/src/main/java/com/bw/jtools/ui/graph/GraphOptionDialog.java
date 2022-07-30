package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyChangeListener;
import com.bw.jtools.properties.PropertyEnumValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.properties.PropertyPaintValue;
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

		addProperty(p, new PropertyNumberValue("Zoom", v.getVisualSettings().scale_), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Number n = ((PropertyNumberValue) value).getValue();
			nv.getVisualSettings().scale_ = (n == null) ? 1 : n.floatValue();
			if (nv.getVisualSettings().scale_ <= 0)
			{
				nv.getVisualSettings().scale_ = 1;
				((PropertyNumberValue) value).setValue(nv.getVisualSettings().scale_);
			}
			graphPanel.repaint();
		});


		addProperty(p, new PropertyPaintValue("Edge", v.getVisualSettings().edge_.color), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Color c = ((PropertyPaintValue) value).getColorValue();
			nv.getVisualSettings().edge_.color = (c == null) ? Color.BLACK : c;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyNumberValue("Edge Width", v.getVisualSettings().edge_.width), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Number w = ((PropertyNumberValue) value).getValue();
			nv.getVisualSettings().edge_.width = (w == null) ? 1 : w.floatValue();
			graphPanel.repaint();
		});

		addProperty(p, new PropertyEnumValue<EdgeMode>("Edge Type", v.getVisualSettings().edge_.mode), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			EdgeMode e = ((PropertyEnumValue<EdgeMode>) value).getValue();
			nv.getVisualSettings().edge_.mode = (e == null) ? EdgeMode.STRAIGHT : e;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyBooleanValue("Edge Decorate", v.getVisualSettings().edge_.decorate), value ->
		{
			getVisualSettings().edge_.decorate = getBoolean(value, getVisualSettings().edge_.decorate);
			graphPanel.repaint();
		});

		addProperty(p, new PropertyNumberValue("Edge Snake Factor", v.getVisualSettings().edge_.snakeFactor), value ->
		{
			Number w = ((PropertyNumberValue) value).getValue();
			getVisualSettings().edge_.snakeFactor = w.floatValue();
			graphPanel.repaint();
		});

		addProperty(p, new PropertyBooleanValue("Node opaque", v.getVisualSettings().node_.opaque), value ->
		{
			getVisualSettings().node_.opaque = getBoolean(value, getVisualSettings().node_.opaque);
			graphPanel.repaint();
		});

		addProperty(p, new PropertyPaintValue("Node Border", v.getVisualSettings().node_.border), value ->
		{
			Color c = ((PropertyPaintValue) value).getColorValue();
			getVisualSettings().node_.border = (c == null) ? Color.LIGHT_GRAY : c;
			graphPanel.repaint();
		});

		addProperty(p, new PropertyPaintValue("Node Background", v.getVisualSettings().node_.background), value ->
		{
			Color c = ((PropertyPaintValue) value).getColorValue();
			getVisualSettings().node_.background = (c == null) ? Color.LIGHT_GRAY : c;
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

		addProperty(p, new PropertyBooleanValue("Debug", v.getVisualSettings().debug_), value ->
		{
			getVisualSettings().debug_ = getBoolean(value, getVisualSettings().debug_);
			graphPanel.repaint();
		});

		PropertyGroupNode root = new PropertyGroupNode(null);
		root.add(new PropertyGroupNode(p));

		model.setRoot(root);
		table.expandAll();
		// table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

	}
}