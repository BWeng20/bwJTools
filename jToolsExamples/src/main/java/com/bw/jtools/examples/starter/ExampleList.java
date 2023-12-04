/*
 * Copyright Bernd Wengenroth.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bw.jtools.examples.starter;

import com.bw.jtools.image.ImageTool;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JExceptionDialog;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Main for Example Jar with Demo-selection.
 */
public final class ExampleList extends JFrame
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -1470942606169822654L;

	static String[][] examples =
			{
					{"Application Icons", "com.bw.jtools.examples.applicationicons.ApplicationIconsDemo"},
					{"Data Table", "com.bw.jtools.examples.datatable.DataDemo"},
					{"Exception Dialog", "com.bw.jtools.examples.exceptiondialog.JExceptionDialogDemo"},
					{"Property Table", "com.bw.jtools.examples.properties.PropertyTableWindow"},
					{"Property Sheet", "com.bw.jtools.examples.properties.PropertySheetWindow"},
					{"Property Tile", "com.bw.jtools.examples.properties.PropertyTileWindow"},
					{"Property Multiple", "com.bw.jtools.examples.properties.PropertyDemoApplication"},
					{"Tab Component", "com.bw.jtools.examples.tabcomponent.JTabComponentDemo"},
					{"Path Chooser", "com.bw.jtools.examples.pathchooser.JPathChooserDemo"},
					{"Swing UI-Default", "com.bw.jtools.examples.uidefaults.UIDefaults"},
					{"SVG Demonstration", "com.bw.jtools.examples.svg.SVGDemo"},
					{"SVG Viewer from jSVG Examples", "com.bw.jtools.examples.svg.SVGViewer"},
					{"L-System Demonstration", "com.bw.jtools.examples.lsystem.LSystemDemo"},
					{"JTransformPanel Demonstration", "com.bw.jtools.examples.tranformpanel.JTransformPanelDemo"}
			};

	private class ExampleWindowWatcher extends WindowAdapter
	{
		int count = 0;

		@Override
		public void windowClosed(WindowEvent e)
		{
			if (--count == 0)
			{
				setVisible(true);
			}
			e.getWindow()
			 .removeWindowListener(this);
		}

		ExampleWindowWatcher()
		{
			Window[] ws = Window.getOwnerlessWindows();
			if (ws != null)
			{
				Window myWindow = SwingUtilities.getWindowAncestor(ExampleList.this);
				for (Window w : ws)
				{
					if (w != myWindow && w.isShowing())
					{
						++count;
						w.addWindowListener(this);
					}
				}
			}
			if (count == 0)
				setVisible(true);
		}
	}

	public ExampleList()
	{
		super(I18N.getText("Demo.Title"));

		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel testInfoPanel = new JPanel(new GridBagLayout());
		JPanel testListPanel = new JPanel(new GridBagLayout());

		GridBagConstraints iC = new GridBagConstraints();
		iC.gridx = 0;
		iC.gridy = GridBagConstraints.RELATIVE;
		iC.anchor = GridBagConstraints.LINE_START;
		iC.weightx = 1;
		iC.weighty = 1;
		iC.insets = new Insets(10, 5, 0, 5);
		iC.fill = GridBagConstraints.HORIZONTAL;

		for (String[] r : examples)
		{
			final String name = r[0];
			final String clazz = r[1];
			final String lang = Locale.getDefault()
									  .getLanguage();

			try
			{
				Class c = Class.forName(clazz);

				String description = null;
				String screenShot = null;
				try
				{
					// try to get additional descrption from some static field "DESCRIPTION"
					String[][] desc = (String[][]) c.getField("DESCRIPTION")
													.get(null);

					for (String[] d : desc)
						if (new Locale(d[0]).getLanguage()
											.equals(lang))
						{
							description = d[1];
							if (d.length > 2)
								screenShot = d[2];
							break;
						}
					if (description == null && desc.length > 0)
					{
						description = desc[0][1];
						if (desc[0].length > 2)
							screenShot = desc[0][2];
					}

					Icon ic = null;
					if (screenShot != null)
					{
						ic = new ImageIcon(ImageTool.getImageSafe(Class.forName(clazz), screenShot));
					}
					demoScreenShots.put(clazz, ic);


				}
				catch (Exception fc)
				{
				}
				if (description == null)
					description = "";

				JButton start = new JButton("<html><h3>" + name + "</h3><p style='color:#505050;'>" + description + "</p><p>&nbsp;</p></html>");
				start.setHorizontalAlignment(SwingConstants.LEADING);
				start.addActionListener((evt) ->
				{
					setDemoInfos(name, clazz);
				});
				testListPanel.add(start, iC);
			}
			catch (Exception e)
			{

			}
		}

		start = new JButton("<html><h3>Start</h3></html>");
		start.setIconTextGap(10);
		start.addActionListener((evt) ->
		{
			try
			{
				setVisible(false);
				Method main = Class.forName(currentDemoClazz)
								   .getMethod("main", String[].class);
				main.invoke(null, new Object[]{new String[0]});
				new ExampleWindowWatcher();

			}
			catch (Exception e)
			{
				e.printStackTrace();
				;
				JExceptionDialog ed = new JExceptionDialog(this, "Example", "Failed to start '" + currentDemoName + "'", e);
				ed.setLocationByPlatform(true);
				ed.setVisible(true);
				setVisible(true);
			}
		});

		GridBagConstraints gc = new GridBagConstraints();

		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.weightx = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(10, 10, 5, 10);

		gc.fill = GridBagConstraints.HORIZONTAL;
		infoDescription = new JLabel();
		testInfoPanel.add(infoDescription, gc);
		infoIcon = new JLabel();

		Dimension icSize = new Dimension(0, 0);
		for (Icon ic : demoScreenShots.values())
		{
			if (ic != null)
			{
				icSize.height = Math.max(icSize.height, ic.getIconHeight());
				icSize.width = Math.max(icSize.width, ic.getIconWidth());
			}
		}
		infoIcon.setPreferredSize(icSize);
		gc.gridy++;
		testInfoPanel.add(infoIcon, gc);

		gc.fill = GridBagConstraints.NONE;
		gc.gridy++;
		gc.anchor = GridBagConstraints.SOUTHEAST;
		gc.weighty = 1;
		testInfoPanel.add(start, gc);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JScrollPane scrollPane = new JScrollPane(testListPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		content.add(JSplitPane.LEFT, scrollPane);
		content.add(JSplitPane.RIGHT, testInfoPanel);
		setContentPane(content);

		Dimension screen = java.awt.Toolkit.getDefaultToolkit()
										   .getScreenSize();

		pack();
		Dimension d = getSize();
		if (d.height > 0.5 * screen.height)
		{
			setPreferredSize(new Dimension(d.width, (int) (0.5 * screen.height)));
		}
		testListPanel.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width -
				((Integer) UIManager.get("ScrollBar.width")).intValue(), testListPanel.getPreferredSize().height));
		pack();

		setLocationByPlatform(true);
		setVisible(true);
	}

	String currentDemoClazz;
	String currentDemoName;
	JButton start;
	JLabel infoIcon;
	JLabel infoDescription;

	Map<String, Icon> demoScreenShots = new HashMap<>();

	private void setDemoInfos(String name, String clazz)
	{

		final String lang = Locale.getDefault()
								  .getLanguage();

		try
		{
			Class c = Class.forName(clazz);

			String description = null;
			String screenShot = null;
			try
			{
				// try to get additional description from some static field "DESCRIPTION"
				String[][] desc = (String[][]) c.getField("DESCRIPTION")
												.get(null);

				for (String[] d : desc)
					if (new Locale(d[0]).getLanguage()
										.equals(lang))
					{
						description = d[1];
						if (d.length > 2)
							screenShot = d[2];
						break;
					}
				if (description == null && desc.length > 0)
				{
					description = desc[0][1];
					if (desc[0].length > 2)
						screenShot = desc[0][2];
				}

			}
			catch (Exception fc)
			{
			}
			if (description == null)
				description = "";

			infoDescription.setText("<html><html><h3>" + name + "</h3><p style='color:#505050;'>" + description + "</p><p>&nbsp;</p></h3></html>");
			Icon ic = demoScreenShots.get(clazz);
			infoIcon.setIcon(ic);

			currentDemoClazz = clazz;
			currentDemoName = name;
		}
		catch (Exception e)
		{

		}
	}


	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		I18N.addBundle("com.bw.jtools.examples.starter.i18n", ExampleList.class);
		new ExampleList();

	}

}
