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
package com.bw.jtools.examples.lsystem;

import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.graph.LSystem;
import com.bw.jtools.graph.LSystemConfig;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.lsystem.LSystemConfigDialog;
import com.bw.jtools.ui.lsystem.LSystemPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class LSystemDemo
{
	JFrame frame_;
	JButton optionButton_;
	LSystemPanel lsysPanel_;

	boolean debug_ = false;

	public LSystemDemo()
	{
		Application.initialize(LSystemDemo.class);

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		frame_ = new JFrame("L-System Demonstration");

		JPanel mainPanel = new JPanel(new BorderLayout());
		frame_.setContentPane(mainPanel);

		lsysPanel_ = new LSystemPanel();

		/*
		LSystem lsys = new LSystem("X",
				Math.toRadians(90), Map.of('X', "YF-", 'Y', "YX-"));
		 */

		/* Sierpinski triangle
		LSystem lsys = new LSystem("A",
				Math.toRadians(60), Map.of('A', "B-A-B", 'B', "A+B+A"));
		lsys.setCommand('A', LSystemGraphicCommand.DRAW_FORWARD);
		lsys.setCommand('B', LSystemGraphicCommand.DRAW_FORWARD);
		*/

		/** Fractal plant */
		LSystem lsys = new LSystem(new LSystemConfig("X",
				Math.toRadians(25),
				Map.of('X', "F+[[X]-X]-F[-FX]+X", 'F', "FF")));

		lsys.getConfig().deltaX_ = 5;
		lsys.getConfig().deltaX_ = -8;
		lsysPanel_.setLSystem(lsys);

		lsysPanel_.setMinimumSize(new Dimension(100, 100));
		JScrollPane lsysScrollPanel = new JScrollPane(lsysPanel_);
		lsysScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lsysScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(lsysScrollPanel, BorderLayout.CENTER);

		JPanel statusLine = new JPanel(new BorderLayout(10, 0));
		statusLine.add(new JLAFComboBox(), BorderLayout.WEST);

		JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton gen = new JButton("Gen+");
		gen.addActionListener(e ->
		{
			lsys.generation();
			if (debug_)
			{
				System.out.println("==================");
				System.out.println(lsys.getCurrent());
				System.out.println("==================");
			}
			lsysPanel_.updateLSystem();
		});
		ctrl.add(gen);

		JButton reset = new JButton("<html>Gen<b>0</b></html>");
		reset.addActionListener(e ->
		{
			lsys.reset();
			lsysPanel_.updateLSystem();
		});
		ctrl.add(reset);

		optionButton_ = new JButton("\u270E"); // Unicode Pencil
		optionButton_.addActionListener(e -> showOptions());
		ctrl.add(optionButton_);

		statusLine.add(ctrl, BorderLayout.EAST);

		JLabel fps = new JLabel("...");
		statusLine.add(fps, BorderLayout.CENTER);

		mainPanel.add(statusLine, BorderLayout.SOUTH);

		frame_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame_.setIconImages(IconTool.getAppIconImages());
		frame_.pack();

		// Restore window-position and dimension from preferences.
		SettingsUI.loadWindowPosition(frame_);
		SettingsUI.storePositionAndFlushOnClose(frame_);

		frame_.setVisible(true);

		Timer fpsTimer = new Timer(1000, e ->
		{
			StringBuilder sb = new StringBuilder();
			if (lsysPanel_.paintCount_ > 0)
			{
				sb.append(lsysPanel_.paintCount_)
				  .append(" fps ");
			}
			sb.append(" Gen ")
			  .append(lsys.getGenerations());
			sb.append(" (")
			  .append(lsys.getCurrent()
						  .length())
			  .append(" chars)");
			fps.setText(sb.toString());
			lsysPanel_.paintCount_ = 0;
		});
		fpsTimer.start();

		frame_.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (options != null)
				{
					options.setVisible(false);
					options.dispose();
					options = null;
				}
				fpsTimer.stop();
			}
		});

		Log.info("Started");
	}

	LSystemConfigDialog options;

	public void showOptions()
	{
		if (options != null && options.isVisible())
		{
			options.setVisible(false);
		}
		else
		{
			if (options == null)
			{
				options = new LSystemConfigDialog(lsysPanel_);
				options.pack();
			}

			Point l = optionButton_.getLocationOnScreen();
			l.x -= options.getWidth() / 2;
			l.y -= options.getHeight() / 2;
			options.setLocation(l);
			options.setVisible(true);
		}
	}

	static public void main(String[] args)
	{
		new LSystemDemo();
	}
}
