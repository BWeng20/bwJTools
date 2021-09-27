package com.bw.jtools.examples.svg;

import com.bw.jtools.Application;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.shape.ShapeIcon;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.svg.SVGConverter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SVGDemo
{
	public static final String[][] DESCRIPTION =
	{
			{ "en", "Demonstrates usage of SVG." },
			{ "de", "Demonstriert die Verwendung von SVG." }
	};


	public static void main(String[] args)
	{
		Application.initialize(SVGDemo.class);

		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}


		JPanel input = new JPanel(new BorderLayout());
		JButton render = new JButton("Show");
		JButton load = new JButton("Load");
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		final List<ShapeIcon> icons = new ArrayList<>();

		JTextArea data = new JTextArea(5, 100);

		input.add(BorderLayout.CENTER, new JScrollPane(data));

		JPanel drawPanel = new JPanel(new BorderLayout(10, 10));
		JPanel controls = new JPanel(new GridLayout(0, 1));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(load);
		buttons.add(render);
		controls.add(buttons);

		double sliderFactor = 100;

		Hashtable<Integer, String> labels = new Hashtable(
				Map.of((int) (0 * sliderFactor), new JLabel("0"),
						(int) (1 * sliderFactor), new JLabel("1"),
						(int) (2 * sliderFactor), new JLabel("2"),
						(int) (4 * sliderFactor), new JLabel("4"),
						(int) (8 * sliderFactor), new JLabel("8"),
						(int) (10 * sliderFactor), new JLabel("10"),
						(int) (15 * sliderFactor), new JLabel("15"),
						(int) (20 * sliderFactor), new JLabel("20")
				));

		final JSlider scaleX = new JSlider(JSlider.HORIZONTAL, 0, (int) (20 * sliderFactor), (int) sliderFactor);
		scaleX.setPaintLabels(false);
		scaleX.setPaintTicks(false);
		scaleX.setExtent((int) (sliderFactor / 10));

		final JSlider scaleY = new JSlider(JSlider.HORIZONTAL, 0, (int) (20 * sliderFactor), (int) sliderFactor);
		scaleY.setLabelTable(labels);
		scaleY.setPaintLabels(true);
		scaleY.setMajorTickSpacing((int) (sliderFactor / 2));
		scaleY.setMinorTickSpacing((int) (sliderFactor / 10));
		scaleY.setPaintTicks(true);
		scaleY.setExtent((int) (sliderFactor / 10));

		controls.add(scaleX);
		controls.add(scaleY);
		input.add(BorderLayout.LINE_END, controls);

		JLabel draw = new JLabel();
		draw.setHorizontalAlignment(JLabel.LEFT);
		draw.setVerticalAlignment(JLabel.TOP);

		drawPanel.add(BorderLayout.CENTER, draw);
		drawPanel.setPreferredSize(new Dimension(200, 600));

		JTextField status = new JTextField();
		status.setEditable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.NORTH, input);
		panel.add(BorderLayout.CENTER, drawPanel);
		panel.add(BorderLayout.SOUTH, status);

		Runnable updateStatus = () ->
		{
			status.setText(draw.getIcon()
							   .getIconWidth() + "x" + draw.getIcon()
														   .getIconHeight() + ", scale " +
					nf.format(scaleX.getValue() / sliderFactor) + " x " + nf.format(scaleY.getValue() / sliderFactor)
			);
		};

		ChangeListener sliderCl = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				SwingUtilities.invokeLater(() ->
				{
					double x = scaleX.getValue() / sliderFactor;
					double y = scaleY.getValue() / sliderFactor;

					if (x < 0.1d)
					{
						scaleX.setValue((int) (0.1d * sliderFactor));
						x = 0.1;
					}
					if (y < 0.1d)
					{
						scaleY.setValue((int) (0.1d * sliderFactor));
						x = 0.1;
					}

					boolean repaint = false;
					for (ShapeIcon i : icons)
					{
						if (x != i.getXScale() || y != i.getYScale())
						{
							repaint = true;
							i.setScale(x, y);
						}
					}
					if (repaint)
					{
						updateStatus.run();
						drawPanel.repaint();
					}
				});
			}
		};

		scaleX.addChangeListener(sliderCl);
		scaleY.addChangeListener(sliderCl);

		final Runnable doRender = () ->
		{
			try
			{
				SVGConverter svg = new SVGConverter(data.getText());

				ShapeIcon icon = new ShapeIcon(svg.getShapes());
				icon.setInlineBorder(true);


				Dimension d = drawPanel.getSize();

				double scale = Math.min(d.width / (double) icon.getIconWidth(), d.height / (double) icon.getIconHeight());
				icon.setScale(scale, scale);
				scaleX.setValue((int) (0.5 + scale * sliderFactor));
				scaleY.setValue((int) (0.5 + scale * sliderFactor));

				icons.clear();
				icons.add(icon);
				draw.setIcon(icon);
				updateStatus.run();

				drawPanel.invalidate();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				status.setText(ex.getMessage());
			}
		};

		render.addActionListener(e ->
		{
			doRender.run();
		});

		JFrame f = new JFrame("SVG Demonstration");
		f.setIconImages( IconTool.getAppIconImages() );

		load.addActionListener(e ->
		{
			File file = IOTool.selectFile(f, "svg_file", "Select SVG", IOTool.OPEN, new FileNameExtensionFilter("SVG Files", "svg"));
			if (file != null)
			{
				try
				{
					byte d[] = Files.readAllBytes(file.toPath());
					data.setText(new String(d, StandardCharsets.UTF_8));
					SwingUtilities.invokeLater(() ->
					{
						doRender.run();
					});
				}
				catch (Exception ex)
				{
					JExceptionDialog d = new JExceptionDialog(f, ex);
					d.setLocationByPlatform(true);
					d.setVisible(true);
				}
			}
		});

		f.setContentPane(panel);
		f.pack();

		// Restore window-position and dimension from prefences.
		SettingsUI.loadWindowPosition(f);
		SettingsUI.storePositionAndFlushOnClose(f);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}
