package com.bw.jtools.examples.uidefaults;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.examples.applicationicons.ApplicationIconsDemo;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.icon.DummyIcon;
import com.bw.jtools.ui.icon.JPaintIcon;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UIDefaults
{

	public static final String[][] DESCRIPTION =
			{
					{"en", "Shows Swing Default Icons and Color"},
					{"de", "Zeigt Swings Standard-Icons und -Farben an"}
			};

	static JFrame frame;
	static Icon dummy_ = new DummyIcon();

	static class ColorWrapper
	{

		String name;
		Color color;

		ColorWrapper(String name, Color color)
		{
			this.name = name;
			this.color = color;
		}
	}

	static class IconWrapper implements Icon
	{

		Icon icon;

		IconWrapper(Icon icon)
		{
			this.icon = icon;
			if (icon == null) this.icon = new DummyIcon();
		}

		@Override
		public int getIconHeight()
		{
			return icon.getIconHeight();
		}

		@Override
		public int getIconWidth()
		{
			return icon.getIconWidth();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			try
			{
				icon.paintIcon(c, g, x, y);
			}
			catch (Exception e)
			{
				dummy_.paintIcon(c, g, x, y);
			}
		}
	}

	static class ColorListCellRenderer extends DefaultListCellRenderer
	{

		private JLabel label;
		private JPaintIcon icon;

		ColorListCellRenderer()
		{
			icon = new JPaintIcon();
			label = new JLabel();
			label.setOpaque(true);
			label.setIconTextGap(10);
			label.setIcon(icon);
			Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
			label.setFont(f);
		}

		@Override
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean selected,
				boolean expanded)
		{

			ColorWrapper cw = (ColorWrapper) value;

			icon.setPaint(cw.color);
			label.setText(UITool.paintToString(cw.color));

			if (selected)
			{
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			}
			else
			{
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			return label;
		}
	}

	protected static ListModel createDefaultColorModel()
	{
		DefaultListModel listModel = new DefaultListModel();

		HashMap<Object, Object> def2 = new HashMap(UIManager.getLookAndFeelDefaults());

		List<ColorWrapper> w = new ArrayList<>();
		for (Map.Entry<Object, Object> entry : def2.entrySet())
		{
			String k = String.valueOf(entry.getKey());
			try
			{
				Object v = entry.getValue();
				if (v instanceof Color)
				{
					Color c = (Color) v;
					w.add(new ColorWrapper(k, c));
				}
			}
			catch (Exception e)
			{
			}
		}
		w.sort((c1, c2) ->
		{
			return c1.name.compareTo(c2.name);
		});
		for (ColorWrapper cw : w)
			listModel.addElement(cw);
		return listModel;
	}

	static public void main(String args[])
	{
		// Initialize library.
		Application.initialize(ApplicationIconsDemo.class);

		// The library has now initialized itself from the defaultsettings.properties.
		// parallel to the main-class.


		frame = new JFrame("UI Defaults");

		JTabbedPane tabs = new JTabbedPane();

		JPanel iconTab = new JPanel(new BorderLayout());

		JLabel label = new JLabel(
				"<html><body><b>" +
						"The list contain all UIDefaults Icons. Some may not work outside their component! In this case a red X is shown." +
						"</b></body></html>");
		label.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 20));
		iconTab.add(label, BorderLayout.NORTH);

		JPanel defaultIcons = new JPanel(new GridLayout(0, 2));
		defaultIcons.setBackground(Color.WHITE);
		JList defaultColors = new JList();

		SwingUtilities.invokeLater(() ->
		{
			HashMap<Object, Object> def2 = new HashMap();

			javax.swing.UIDefaults d = UIManager.getLookAndFeelDefaults();

			// We can't iterate via the UIDefauls directly, because the gets call modify the hashmap.
			Set<Object> keys = new HashSet<>(d.keySet());
			for (Object k : keys)
			{
				def2.put(k, d.get(k));
			}

			Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
			for (Map.Entry<Object, Object> entry : def2.entrySet())
			{
				try
				{
					Object v = entry.getValue();
					if (v instanceof Icon)
					{
						String k = String.valueOf(entry.getKey());
						Icon i = (Icon) v;

						JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
						p.setBackground(Color.WHITE);

						JLabel iconlabel = new JLabel();
						iconlabel.setIcon(new IconWrapper(i));

						JLabel textlabel = new JLabel();
						textlabel.setText(k);
						textlabel.setFont(f);

						p.add(textlabel);
						p.add(iconlabel);

						defaultIcons.add(p);
					}
				}
				catch (Exception e)
				{
				}

				defaultColors.setModel(createDefaultColorModel());

			}
		});
		iconTab.add(new JScrollPane(defaultIcons), BorderLayout.CENTER);
		tabs.addTab("Icons", iconTab);

		defaultColors.setCellRenderer(new ColorListCellRenderer());

		tabs.addTab("Colors", new JScrollPane(defaultColors));

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setContentPane(tabs);
		frame.pack();

		// Restore window-position and dimension from prefences.
		SettingsUI.loadWindowPosition(frame);
		SettingsUI.storePositionAndFlushOnClose(frame);
		frame.setVisible(true);

		Log.info("Started");

	}


}
