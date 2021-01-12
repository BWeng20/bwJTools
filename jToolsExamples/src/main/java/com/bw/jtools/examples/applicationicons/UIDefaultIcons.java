package com.bw.jtools.examples.applicationicons;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.DummyIcon;
import com.bw.jtools.ui.SettingsUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class UIDefaultIcons
{
	static JFrame frame;

	static class IconWrapper implements Icon {

		String name;
		Icon icon;

		IconWrapper( String name, Icon icon) {
			this.name = name;
			this.icon = icon;
			if ( icon == null ) this.icon = new DummyIcon();
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
			catch (ClassCastException e)
			{
				icon = new DummyIcon();
				icon.paintIcon(c, g, x, y);
			}
		}
	}

	static class IconListCellRenderer extends DefaultListCellRenderer {

		private JLabel label;

		IconListCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			label.setIconTextGap(10);
			Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
			label.setFont( f );
		}

		@Override
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean selected,
				boolean expanded) {

			IconWrapper iw = (IconWrapper)value;

			label.setIcon(iw);
			label.setText(iw.name);

			if (selected) {
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			} else {
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			return label;
		}
	}

	protected static ListModel createDefaultIconModel()
	{

		DefaultListModel listModel = new DefaultListModel();
		UIDefaults uiDef = UIManager.getLookAndFeelDefaults();

		for (Map.Entry<Object, Object> entry : uiDef.entrySet())
		{
			String k = String.valueOf(entry.getKey());
			try {
				Icon i = uiDef.getIcon(k);
				if ( i != null )
					listModel.addElement( new IconWrapper(k, i) );
			}
			catch ( Exception e) {
			}

		}
		return listModel;
	}

	static public void main( String args[] )
	{
		// Initialize library.
		Application.initialize(ApplicationIconsDemo.class);

		// The library has now initialized itself from the defaultsettings.properties.
		// parallel to the main-class.

		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		frame = new JFrame("UI Default Icons");
		JPanel panel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(
				"<html><body><b>"+
						"The list contain all UIDefaults Icons. Some may not work outside their component!"+
						"</b></body></html>" );
		label.setBorder(BorderFactory.createEmptyBorder(20,5,20,20));
		panel.add( label, BorderLayout.NORTH );

		JList defaultIcons = new JList( createDefaultIconModel() );
		defaultIcons.setFixedCellHeight(40);
		defaultIcons.setCellRenderer(new IconListCellRenderer() );
		panel.add( new JScrollPane(defaultIcons), BorderLayout.CENTER );

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setContentPane(panel);
		frame.pack();

		// Restore window-position and dimension from prefences.
		SettingsUI.loadWindowPosition(frame);
		SettingsUI.storePositionAndFlushOnClose( frame );
		frame.setVisible(true);

		Log.info("Started");

	}
}
