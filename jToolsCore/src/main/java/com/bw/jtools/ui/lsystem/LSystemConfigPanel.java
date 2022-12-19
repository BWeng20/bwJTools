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
package com.bw.jtools.ui.lsystem;

import com.bw.jtools.graph.LSystemConfig;
import com.bw.jtools.graph.LSystemGraphicCommand;
import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyMapValue;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.properties.PropertyStringValue;
import com.bw.jtools.ui.properties.PropertyPanelBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.swing.tree.DefaultTreeModel;
import java.util.Map;
import java.util.stream.Collectors;

public class LSystemConfigPanel extends PropertyPanelBase
{
	private LSystemPanel lSystemPanel_;
	private LSystemConfig lSystemConfig_;

	public LSystemConfigPanel(LSystemPanel lpanel)
	{
		this.lSystemPanel_ = lpanel;
		this.lSystemConfig_ = lpanel.getLSystem()
									.getConfig();
		init();
	}

	protected void init()
	{
		DefaultTreeModel model = table_.getTreeModel();

		PropertyGroup visualSettings = new PropertyGroup("Visual Settings");

		addProperty(visualSettings, new PropertyBooleanValue("Border", lSystemPanel_.isDrawBorder()), value ->
		{
			Boolean n = value.getValue();
			lSystemPanel_.setDrawBorder(n);
		});

		addProperty(visualSettings, new PropertyNumberValue("Angel (degree)",
				Math.toDegrees(lSystemConfig_.angle_)), value ->
		{
			double d = Math.toRadians(value.getValue()
										   .doubleValue());
			lSystemPanel_.getLSystem()
						 .getConfig().angle_ = d;
			lSystemPanel_.updateLSystem();
		});

		addProperty(visualSettings, new PropertyNumberValue("Delta X", lSystemConfig_.deltaX_), value ->
		{
			double d = value.getValue()
							.doubleValue();
			lSystemPanel_.getLSystem()
						 .getConfig().deltaX_ = d;
			lSystemPanel_.updateLSystem();
		});

		addProperty(visualSettings, new PropertyNumberValue("Delta Y", lSystemConfig_.deltaY_), value ->
		{
			double d = value.getValue()
							.doubleValue();
			lSystemPanel_.getLSystem()
						 .getConfig().deltaY_ = d;
			lSystemPanel_.updateLSystem();
		});

		PropertyGroup definition = new PropertyGroup("Definition");
		addProperty(definition, new PropertyStringValue("Axiom", lSystemConfig_.axiom_), value ->
		{
			lSystemPanel_.getLSystem()
						 .getConfig().axiom_ = value.getValue();
			lSystemPanel_.updateLSystem();
		});
		PropertyMapValue pRules = new PropertyMapValue("Rules", Character.class, String.class);
		pRules.putAll(lSystemConfig_.rules_);
		addProperty(definition, pRules, value ->
		{
			lSystemConfig_.rules_.clear();
			lSystemConfig_.rules_.putAll((Map) value.getValue());
			lSystemPanel_.updateLSystem();
		});
		PropertyMapValue pCommands = new PropertyMapValue("Commands", Character.class, LSystemGraphicCommand.class);
		pCommands.putAll(lSystemConfig_.commands_);
		addProperty(definition, pCommands, value ->
		{
			lSystemConfig_.commands_.clear();
			lSystemConfig_.commands_.putAll((Map) value.getValue());
			lSystemPanel_.updateLSystem();
		});

		PropertyGroupNode root = new PropertyGroupNode(null);
		root.add(new PropertyGroupNode(visualSettings));
		root.add(new PropertyGroupNode(definition));

		model.setRoot(root);
		table_.expandAll();
	}

}
