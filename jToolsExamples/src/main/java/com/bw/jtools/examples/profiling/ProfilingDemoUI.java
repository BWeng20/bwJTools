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
package com.bw.jtools.examples.profiling;

import com.bw.jtools.Application;
import com.bw.jtools.examples.exceptiondialog.JExceptionDialogDemo;
import com.bw.jtools.ui.icon.IconTool;
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
