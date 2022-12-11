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
package com.bw.jtools.ui.dropcaps;

import com.bw.jtools.image.BlendComposite;
import com.bw.jtools.properties.PropertyChangeClient;
import com.bw.jtools.properties.PropertyFontValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyPaintValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyTable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class JDropCapsConfig extends JPanel
{
	JDropCapsLabel dropCap_;
	PropertyChangeClient propClient_ = new PropertyChangeClient();

	private static String getNameOfComposite(Composite c)
	{
		if (c == null)
			return "None";
		else if (c instanceof BlendComposite)
		{
			return ((BlendComposite) c).getMode()
									   .toString();
		}
		else if (c == AlphaComposite.Clear)
			return "Clear";
		else if (c == AlphaComposite.Xor)
			return "Xor";
		else if (c == AlphaComposite.Dst)
			return "Destination";
		else if (c == AlphaComposite.DstAtop)
			return "Destination Atop";
		else if (c == AlphaComposite.DstIn)
			return "Destination In";
		else if (c == AlphaComposite.DstOut)
			return "Destination Out";
		else if (c == AlphaComposite.SrcAtop)
			return "Source Atop";
		else if (c == AlphaComposite.DstOver)
			return "Destination Over";
		else if (c == AlphaComposite.SrcOver)
			return "Source Over";
		else if (c == AlphaComposite.Src)
			return "Source";
		else if (c == AlphaComposite.SrcIn)
			return "Source In";
		else if (c == AlphaComposite.SrcOut)
			return "Source Out";
		else
			return "None";
	}

	public JDropCapsConfig(JDropCapsLabel dropCap)
	{
		dropCap_ = dropCap;

		setLayout(new BorderLayout());

		PropertyTable props = new PropertyTable();
		PropertyGroup root = new PropertyGroup(null);

		PropertyFontValue fontProp = new PropertyFontValue("font", dropCap_.getFont());
		propClient_.addProperty(root, fontProp,
				v -> dropCap_.setFont(v.getValue()) );

		// Paint-chooser to select the text-color.
		PropertyPaintValue textColorProp = new PropertyPaintValue("textColor", dropCap_.getForegroundPaint());
		textColorProp.setDisplayName(I18N.getText("dropcapconfig.textColor"));
		propClient_.addProperty(root, textColorProp, v -> dropCap_.setForegroundPaint(v.getValue() ));

		// Add a color-button to select the image-base-color.
		PropertyPaintValue imageBaseColorProp = new PropertyPaintValue("imageBaseColor", dropCap_.getImageBasePaint());
		imageBaseColorProp.setDisplayName(I18N.getText("dropcapconfig.imageBaseColor"));
		propClient_.addProperty(root, imageBaseColorProp, v -> {
			dropCap_.setImageBasePaint(v.getValue());
			dropCap_.repaint();
		});

		// Add a color-button to select the drop-caps-color.
		PropertyPaintValue dropCapColorProp = new PropertyPaintValue("dropCapColor", dropCap_.getDropCapPaint());
		dropCapColorProp.setDisplayName(I18N.getText("dropcapconfig.dropCapColor"));
		propClient_.addProperty(root, dropCapColorProp, v -> dropCap_.setDropCapPaint(v.getValue()) );

		LinkedHashMap<String, Composite> map = new LinkedHashMap<>();
		map.put("None", null);
		for (BlendComposite.Mode m : BlendComposite.Mode.values())
			map.put(m.toString(), new BlendComposite(m));

		for (Composite cp : Arrays.asList(
				AlphaComposite.Clear, AlphaComposite.Xor, AlphaComposite.Dst, AlphaComposite.DstAtop,
				AlphaComposite.DstIn, AlphaComposite.DstOut, AlphaComposite.SrcAtop, AlphaComposite.DstOver,
				AlphaComposite.SrcOver, AlphaComposite.Src, AlphaComposite.SrcIn,
				AlphaComposite.SrcOut))
		{
			map.put(getNameOfComposite(cp), cp);
		}

		PropertyValue<Composite> dropCapModeProp = new PropertyValue("dropCapMode", Composite.class);
		dropCapModeProp.possibleValues_ = map;
		dropCapModeProp.setValue(dropCap_.getDropCapPaintComposite());
		propClient_.addProperty(root, dropCapModeProp, v -> dropCap_.setDropCapPaint(dropCap_.getDropCapPaint(), v.getValue()));

		PropertyGroupNode rootNode = new PropertyGroupNode(null);
		rootNode.add(new PropertyGroupNode(root));

		props.getTreeModel()
			 .setRoot(rootNode);
		props.expandAll();
		add(BorderLayout.CENTER, props);


	}

	public static void showDialog(JDropCapsLabel dropCap, String title)
	{
		// Initialization of font list may take several seconds.
		// Show a wait-cursor.
		Window w = SwingUtilities.getWindowAncestor(dropCap);

		JDropCapsConfig chooserPane = new JDropCapsConfig(dropCap);
		JDialog chooserDialog = new JDialog(w, title, Dialog.ModalityType.APPLICATION_MODAL);

		chooserDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel c = new JPanel();
		chooserDialog.setContentPane(c);
		c.setLayout(new BorderLayout());
		c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.add(chooserPane, BorderLayout.CENTER);

		chooserDialog.pack();

		UITool.placeAtSide(dropCap, chooserDialog);

		chooserDialog.setVisible(true);
	}
}
