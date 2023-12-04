package com.bw.jtools.examples.tranformpanel;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.JTransformPanel;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;

public class JTransformPanelDemo
{
	public static final String[][] DESCRIPTION =
			{
					{"en", "Demonstrates usage of the experimental <b>com.bw.jtools.ui.JTransformPanel</b>."},
					{"de", "Demonstriert die Verwendung des (experimentellen) <b>com.bw.jtools.ui.JTransformPanel</b>."}
			};

	JFrame frame;

	public JTransformPanelDemo()
	{
		// Initialize library.
		Application.initialize(JTransformPanelDemo.class);
		I18N.addBundle("com.bw.jtools.examples.tranformpanel.i18n", JTransformPanelDemo.class);

		// The library is now initialized from the "defaultsettings.properties"
		// parallel to the main-class ( see "resources").

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		frame = new JFrame(I18N.getText("Title"));

		JPanel mainPanel = new JPanel(new BorderLayout());

		JTransformPanel tpanel = new JTransformPanel(new BorderLayout());
		tpanel.at = AffineTransform.getRotateInstance(Math.toRadians(90));
		tpanel.add(new JTextField("Text"), BorderLayout.CENTER);
		tpanel.add(new JLabel("Label South"), BorderLayout.SOUTH);
		tpanel.add(new JLabel("Label North"), BorderLayout.NORTH);

		JPanel ctrlPanel = new JPanel(new BorderLayout());

		JTextField rotate = new JTextField("90");
		ctrlPanel.add(rotate, BorderLayout.CENTER);
		rotate.addActionListener(e ->
		{
			tpanel.at = AffineTransform.getRotateInstance(Math.toRadians(Double.parseDouble(rotate.getText())));
			frame.invalidate();
		});

		JLAFComboBox lafCB = new JLAFComboBox();
		ctrlPanel.add(lafCB, BorderLayout.LINE_START);

		mainPanel.add(tpanel, BorderLayout.CENTER);
		mainPanel.add(ctrlPanel, BorderLayout.SOUTH);
		frame.setContentPane(mainPanel);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImages(IconTool.getAppIconImages());
		frame.pack();

		// Restore window-position and dimension from preferences.
		SettingsUI.loadWindowPosition(frame);
		SettingsUI.storePositionAndFlushOnClose(frame);
		frame.setVisible(true);

		Log.info("Started");

	}

	static public void main(String[] args)
	{
		new JTransformPanelDemo();
	}


}
