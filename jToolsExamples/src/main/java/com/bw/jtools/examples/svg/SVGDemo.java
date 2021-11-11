package com.bw.jtools.examples.svg;

import com.bw.jtools.Application;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.shape.ShapePane;
import com.bw.jtools.svg.SVGConverter;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.JPaintViewport;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.image.ImageTool;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.NumberFormat;

public class SVGDemo
{
	public static final String[][] DESCRIPTION =
			{
					{"en", "Demonstrates usage of SVG."},
					{"de", "Demonstriert die Verwendung von SVG."}
			};

	static long timeMS = 0;

	public static void main(String[] args)
	{
		Application.initialize(SVGDemo.class);

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		JButton render = new JButton("Render");
		JButton load = new JButton("Load");
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);


		JTextArea data = new JTextArea(5, 100);

		final JLabel scaleXL = new JLabel("100%");
		final JSlider scaleX = new JSlider(JSlider.HORIZONTAL, 2, 6000, 100);

		final JLabel scaleYL = new JLabel("100%");
		final JSlider scaleY = new JSlider(JSlider.HORIZONTAL, 2, 6000, 100);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEADING));
		buttons.add(load);
		buttons.add(render);
		buttons.setBorder(new EmptyBorder(5, 5, 10, 0));

		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 4;
		gc.weightx = 1f;
		gc.weighty = 1f;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.LINE_START;

		controls.add(new JScrollPane(data), gc);

		gc.weightx = 0;
		gc.weighty = 0;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.NONE;
		controls.add(buttons, gc);

		gc.gridy++;
		gc.gridwidth = 1;
		controls.add(scaleXL, gc);
		gc.gridx = 2;
		gc.weightx = 0.5f;
		gc.fill = GridBagConstraints.HORIZONTAL;
		controls.add(scaleX, gc);

		gc.gridx = 1;
		gc.weightx = 0;
		gc.gridy++;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;
		controls.add(scaleYL, gc);
		gc.gridx = 2;
		gc.weightx = 0.5f;
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		controls.add(scaleY, gc);

		ShapePane drawPanel = new ShapePane();
		drawPanel.setBackground(Color.WHITE);
		drawPanel.setOpaque(false); //< ScrollPane viewport will clear on paint.

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(controls);

		JScrollPane drawScroll = new JScrollPane();
		JPaintViewport vp = new JPaintViewport();
		vp.setBackgroundImage(ImageTool.createCheckerboardImage(Color.WHITE, new Color(230, 230, 230), 10, 10));
		vp.setOpaque(true);
		drawScroll.setViewport(vp);
		drawScroll.setViewportView(drawPanel);
		drawScroll.setOpaque(false);
		split.setBottomComponent(drawScroll);

		JTextField status = new JTextField();
		status.setEditable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.CENTER, split);
		panel.add(BorderLayout.SOUTH, status);

		Runnable updateStatus = () ->
		{
			Dimension r = drawPanel.getPreferredSize();
			status.setText(r.width + "x" + r.height + ", scale " +
					scaleX.getValue() + "% x " + scaleY.getValue() + "% " +
					((timeMS > 0) ? ", Rendered in " + Double.toString(timeMS / 1000d) + "s" : ""));
			status.setForeground(panel.getForeground());
		};

		ChangeListener sliderCl = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				SwingUtilities.invokeLater(() ->
				{
					double x = scaleX.getValue() / 100d;
					double y = scaleY.getValue() / 100d;
					;

					scaleXL.setText(scaleX.getValue() + "%");
					scaleYL.setText(scaleY.getValue() + "%");

					if (x != drawPanel.getXScale() || y != drawPanel.getYScale())
					{
						drawPanel.setScale(x, y);
					}
					updateStatus.run();
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

				drawPanel.setShapes(svg.getShapes());
				drawPanel.getPainter()
						 .setTimeMeasurementEnabled(true);
				drawPanel.setInlineBorder(true);

				Dimension s = drawScroll.getSize();
				s.width -= 10;
				s.height -= 10;
				Dimension d = drawPanel.getPreferredSize();

				double scale = Math.min(s.width / (double) d.width, s.height / (double) d.height);
				drawPanel.setScale(scale, scale);
				scaleX.setValue((int) (0.5 + scale * 100f));
				scaleY.setValue((int) (0.5 + scale * 100f));

				updateStatus.run();
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
		f.setIconImages(IconTool.getAppIconImages());

		load.addActionListener(e ->
		{
			File file = IOTool.selectFile(f, "svg_file", "Select SVG", IOTool.OPEN, new FileNameExtensionFilter("SVG Files", "svg"));
			if (file != null)
			{
				try
				{
					drawPanel.setShapes(null);

					byte d[] = Files.readAllBytes(file.toPath());
					data.setText(new String(d, StandardCharsets.UTF_8));
					SwingUtilities.invokeLater(() ->
					{
						doRender.run();
					});
				}
				catch (NoSuchFileException ex)
				{
					status.setText("File not found: " + file.getPath());
					status.setForeground(Color.RED);
				}
				catch (Exception ex)
				{
					JExceptionDialog d = new JExceptionDialog(f, ex);
					status.setText(ex.getClass()
									 .getSimpleName() + ": " + ex.getMessage());
					status.setForeground(Color.RED);
					d.setLocationByPlatform(true);
					d.setVisible(true);
				}
			}
		});

		f.setContentPane(panel);
		f.pack();

		// Restore window-position and dimension from preferences.
		SettingsUI.loadWindowPosition(f);
		SettingsUI.storePositionAndFlushOnClose(f);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setVisible(true);

		Timer timer = new Timer(1000, e ->
		{
			timeMS = drawPanel.getPainter()
							  .getMeasuredTimeMS();
			updateStatus.run();
		});
		timer.start();
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				timer.stop();
			}
		});


	}
}
