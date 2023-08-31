/*
 * (c) copyright Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.ui.graph;

import com.bw.jtools.graph.Graph;
import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyEnumValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.properties.PropertyPaintValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyPanelBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.swing.tree.DefaultTreeModel;
import java.awt.Color;

public class GraphOptionPanel extends PropertyPanelBase
{
	GraphPanel graphPanel;

	public GraphOptionPanel(GraphPanel graphPanel)
	{
		this.graphPanel = graphPanel;
		init();
	}

	protected VisualSettings getVisualSettings()
	{
		return graphPanel.getNodeVisual()
						 .getVisualSettings();
	}

	protected int getInt(PropertyValue<?> value, int defaultValue)
	{
		if (value instanceof PropertyNumberValue)
		{
			Number nb = ((PropertyNumberValue) value).getValue();
			if (nb != null)
				return nb.intValue();
		}
		return defaultValue;
	}

	protected boolean getBoolean(PropertyValue<?> value, boolean defaultValue)
	{
		if (value instanceof PropertyBooleanValue)
		{
			Boolean nb = ((PropertyBooleanValue) value).getValue();
			if (nb != null)
				return nb;
		}
		return defaultValue;
	}


	protected void init()
	{
		Graph g = graphPanel.getGraph();
		NodeVisual v = graphPanel.getNodeVisual();

		DefaultTreeModel model = table_.getTreeModel();

		PropertyGroup p = new PropertyGroup("Visual Settings");

		addProperty(p, new PropertyNumberValue("Zoom", v.getVisualSettings().scale_), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			Number n = value.getValue();
			nv.getVisualSettings().scale_ = (n == null) ? 1 : n.floatValue();
			if (nv.getVisualSettings().scale_ <= 0)
			{
				nv.getVisualSettings().scale_ = 1;
				value.setValue(nv.getVisualSettings().scale_);
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
			Number w = value.getValue();
			nv.getVisualSettings().edge_.width = (w == null) ? 1 : w.floatValue();
			graphPanel.repaint();
		});

		addProperty(p, new PropertyEnumValue<>("Edge Type", v.getVisualSettings().edge_.mode), value ->
		{
			NodeVisual nv = graphPanel.getNodeVisual();
			EdgeMode e = value.getValue();
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
			Number w = value.getValue();
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
		table_.expandAll();
		// table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

	}
}