package com.bw.jtools.examples.profiling;

import com.bw.jtools.Application;
import com.bw.jtools.examples.exceptiondialog.JExceptionDialogDemo;
import com.bw.jtools.ui.graphic.IconTool;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;

import javax.swing.*;
import java.awt.*;

public class ProfilingDemoUI
{
	JFrame frame;

	public ProfilingDemoUI()
	{
		// Initialize library.
		Application.initialize(JExceptionDialogDemo.class );

		// The library is now initialized from the "defaultsettings.properties"
		// parallel to the main-class.

		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		frame = new JFrame( "Profiling Agent Demonstration" );

		JPanel mainPanel = new JPanel( new BorderLayout());
		frame.setContentPane(mainPanel);

		JButton startButton = new JButton("Start Worker");
		mainPanel.add(startButton, BorderLayout.NORTH );

		startButton.addActionListener((evt) ->
		{
			ProfilingDemo demo = new ProfilingDemo();
			demo.run();

		} );


		JLAFComboBox lafCB = new JLAFComboBox();
		mainPanel.add(lafCB, BorderLayout.SOUTH );

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImages( IconTool.getAppIconImages() );
		frame.pack();

		// Restore window-position and dimension from prefences.
		SettingsUI.loadWindowPosition(frame);
		SettingsUI.storePositionAndFlushOnClose( frame );

		frame.setVisible(true);
	}
}
