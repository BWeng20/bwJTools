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
import com.bw.jtools.lsystem.LSystem;
import com.bw.jtools.lsystem.LSystemConfig;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.lsystem.LSystemConfigDialog;
import com.bw.jtools.ui.lsystem.LSystemPanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * L-System demonstration. Uses LSystemPanel to draw and
 * LSystemConfigDialog to edit settings.
 */
public class LSystemDemo
{

	public static final String[][] DESCRIPTION =
			{
					{"en", "Demonstrates usage of LSystem, LSystemPanel and LSystemConfigDialog."},
					{"de", "Demonstriert die Verwendung von LSystem, LSystemPanel und LSystemConfigDialog."}
			};

	JFrame frame_;
	JButton optionButton_;
	LSystemPanel lsysPanel_;
	JComboBox<String> presetCombo_;

	/**
	 * Example L-System definitions.
	 */
	static Map<String, String> presets_ = new LinkedHashMap<>();

	static
	{
		/** L-Systems, using JSOPN description supported by LSystemConfig. */
		presets_.put("", null);
		presets_.put("Fractal Binary Tree", "{\"axiom\":\"0\",\"angle\":45.0,\"deltaX\":-1.0,\"deltaY\":-1.0,\"rules\":[{\"char\":\"0\",\"rule\":\"1{0}0\"},{\"char\":\"1\",\"rule\":\"11\"}],\"commands\":[{\"char\":\"0\",\"commands\":[\"DRAW_FORWARD\",\"DRAW_LEAF\"]},{\"char\":\"1\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"f\",\"commands\":[\"MOVE_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"[\",\"commands\":[\"PUSH_ON_STACK\"]},{\"char\":\"{\",\"commands\":[\"PUSH_ON_STACK\",\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"-\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"]\",\"commands\":[\"POP_FROM_STACK\"]},{\"char\":\"}\",\"commands\":[\"POP_FROM_STACK\",\"TURN_CLOCKWISE\"]}]}");
		presets_.put("Sierpinski Triangle", "{\"axiom\":\"F-G-G\",\"angle\":120,\"deltaX\":-1.0,\"deltaY\":-1.0,\"rules\":[{\"char\":\"F\",\"rule\":\"F-G+F+G-F\"},{\"char\":\"G\",\"rule\":\"GG\"}],\"commands\":[{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"G\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"-\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]}]}");
		presets_.put("Sierpinski Arrowhead Curve", "{\"axiom\":\"A\",\"angle\":60,\"deltaX\":1.0,\"deltaY\":1.0,\"rules\":[{\"char\":\"A\",\"rule\":\"B-A-B\"},{\"char\":\"B\",\"rule\":\"A+B+A\"}],\"commands\":[{\"char\":\"A\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"B\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"-\",\"commands\":[\"TURN_CLOCKWISE\"]}]}");
		presets_.put("Fractal Plant", "{\"axiom\":\"X\",\"angle\":25.0,\"deltaX\":-1.0,\"deltaY\":-1.0,\"rules\":[{\"char\":\"X\",\"rule\":\"F+[[X]-X]-F[-FX]+X\"},{\"char\":\"F\",\"rule\":\"FF\"}],\"commands\":[{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"[\",\"commands\":[\"PUSH_ON_STACK\"]},{\"char\":\"-\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"]\",\"commands\":[\"POP_FROM_STACK\"]}]}");
		presets_.put("Dragon Curve", "{\"axiom\":\"FX\",\"angle\":90.0,\"deltaX\":1.0,\"deltaY\":-1.0,\"rules\":[{\"char\":\"X\",\"rule\":\"X+YF+\"},{\"char\":\"Y\",\"rule\":\"-FX-Y\"}],\"commands\":[{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"-\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]}]}");
		presets_.put("Flower", "{\"axiom\":\"++FX\",\"angle\":15,\"deltaX\":-1.0,\"deltaY\":-1.0,\"rules\":[{\"char\":\"X\",\"rule\":\"[(-FX)]+FX\"}],\"commands\":[{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"(\",\"commands\":[\"PUSH_ON_STACK\"]},{\"char\":\")\",\"commands\":[\"POP_ANGLE_FROM_STACK\"]},{\"char\":\"+\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"[\",\"commands\":[\"PUSH_ON_STACK\"]},{\"char\":\"-\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"]\",\"commands\":[\"POP_POS_FROM_STACK\"]}]}");
		presets_.put("Town Plan", "{\"axiom\":\"X\",\"angle\":90.0,\"deltaX\":1.0,\"deltaY\":1.0,\"rules\":[{\"char\":\"X\",\"rule\":\"F[+X]F[-X]+X\"},{\"char\":\"F\",\"rule\":\"FF\"}],\"commands\":[{\"char\":\"F\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"X\",\"commands\":[\"DRAW_FORWARD\"]},{\"char\":\"+\",\"commands\":[\"TURN_CLOCKWISE\"]},{\"char\":\"[\",\"commands\":[\"PUSH_ON_STACK\"]},{\"char\":\"-\",\"commands\":[\"TURN_COUNTERCLOCKWISE\"]},{\"char\":\"]\",\"commands\":[\"POP_FROM_STACK\"]}]}");
	}


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

		LSystem lsys = new LSystem(new LSystemConfig("",
				Math.toRadians(15), Collections.EMPTY_MAP));
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

		JButton reset = new JButton("<html>Gen <b>0</b></html>");
		reset.addActionListener(e ->
		{
			lsys.reset();
			lsysPanel_.updateLSystem();
		});
		ctrl.add(reset);

		presetCombo_ = new JComboBox<>(presets_.keySet()
											   .toArray(new String[0]));
		presetCombo_.addItemListener(e ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				String cfg = presets_.get((String) e.getItem());
				if (cfg != null && !cfg.isEmpty())
				{
					lsys.getConfig()
						.fromJSONString(cfg);
					lsysPanel_.getLSystem()
							  .reset();
					lsysPanel_.updateLSystem();
				}
			}
		});
		// Ensure thet combo is wide enough
		presetCombo_.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXX");
		ctrl.add(presetCombo_);

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

	/**
	 * Show option dialog.
	 */
	public void showOptions()
	{
		if (options != null && options.isVisible())
		{
			options.setVisible(false);
		}
		else
		{
			// Reset preset-selection as user creates now a custon setting..
			presetCombo_.setSelectedIndex(0);
			if (options == null)
			{
				options = new LSystemConfigDialog(lsysPanel_);
			}
			else
			{
				// If we reuse the option dialog, we have to force an update.
				options.updateProperties();
			}
			options.pack();

			UITool.placeOnTop(lsysPanel_, options);
			options.setVisible(true);
		}
	}

	static public void main(String[] args)
	{
		new LSystemDemo();
	}
}
