/*
 * The MIT License
 *
 * Copyright 2020 Bernd Wengenroth.
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
package com.bw.jtools.ui.profiling;

import com.bw.jtools.io.ServiceRunner;
import com.bw.jtools.ui.*;
import com.bw.jtools.ui.TextFieldSettingAdapter;
import com.bw.jtools.ui.icon.IconTool;
import com.bw.jtools.ui.profiling.calltree.ProfilingCallTree;
import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.io.Tail;
import com.bw.jtools.profiling.callgraph.FreeMindGraphRenderer;
import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import com.bw.jtools.profiling.callgraph.JSONCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.Timer;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Panel to scan logs for call graphs.
 *
 * @see #main(java.lang.String[])
 */
public class ProfilingLogUI extends JPanel implements Tail.TailListener
{
	private static boolean addI18nBundle = true;


	/**
	 * Initialize a ProfilingLogViewer.
	 */
	public ProfilingLogUI()
	{
		if (addI18nBundle)
		{
			addI18nBundle = false;
			I18N.addBundle("com.bw.jtools.ui.profiling.i18n", ProfilingLogUI.class);
		}
		initComponents();
	}

	private void initComponents()
	{
		decimalFormat = new DecimalFormat("#.#####");

		DecimalFormatSymbols ds = decimalFormat.getDecimalFormatSymbols();
		ds.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(ds);
		decimalFormat.setDecimalSeparatorAlwaysShown(false);

		freemindFileFilter = new FileNameExtensionFilter(I18N.getText("callgraph.export.freemind"), "mm");

		status_error = I18N.getText("callgraph.status.error");
		status_read = I18N.getText("callgraph.status.read");
		status_idle = I18N.getText("callgraph.status.idle");
		status_closed = I18N.getText("callgraph.status.closed");

		setLayout(new BorderLayout());

		logFilePath = new JTextField(60);
		JLabel fileLabel = UIToolSwing.createI18NLabel("callgraph.log.name");
		startStopFileMonitor = UIToolSwing.createI18NTextButton("callgraph.button.start");
		fileMonitorOpen = UIToolSwing.createI18NTextButton("callgraph.button.open");
		fileLabel.setLabelFor(logFilePath);

		JPanel file = new JPanel(new FlowLayout(FlowLayout.LEADING));
		file.add(fileLabel);
		file.add(logFilePath);
		file.add(startStopFileMonitor);
		file.add(fileMonitorOpen);
		file.setName( I18N.getText("callgraph.log.name"));

		JPanel socket = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel socketLabel = UIToolSwing.createI18NLabel("callgraph.socket.name");
		statusHostInput = new JTextField(30);
		statusPortInput = new JTextField(6);
		socketStartButton = UIToolSwing.createI18NTextButton("callgraph.button.start");
		socketLabel.setLabelFor(statusHostInput);
		socket.add(socketLabel);
		socket.add(statusHostInput);
		socket.add(new JLabel(":"));
		socket.add(statusPortInput);
		socket.add(socketStartButton );

		socket.setName( I18N.getText("callgraph.socket.name"));

		JTabbedPane connections = new JTabbedPane(JTabbedPane.TOP);
		connections.add( socket );
		connections.add( file );

		add(connections, BorderLayout.NORTH);

		JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEADING));
		export = UIToolSwing.createI18NTextButton("callgraph.button.export");
		export.addActionListener((ev) ->
		{
			JSONCallGraphParser.GraphInfo graph = callGraph.getGraph();
			if (graph != null)
			{

				File selectedExportFile = IOTool.selectFile(this, preference_prefix_ + "select.exportfile", I18N.getText("callgraph.export.title"), IOTool.SAVE,
						freemindFileFilter, IOTool.getFileFilterJson());

				if (selectedExportFile != null)
				{
					if (freemindFileFilter.accept(selectedExportFile))
					{
						exportFreeMind(selectedExportFile, graph);
					} else if (IOTool.getFileFilterJson().accept(selectedExportFile))
					{
						exportJson(selectedExportFile, graph);
					}
				}
			}
		});
		JPanel lowerButtons = new JPanel(new FlowLayout(FlowLayout.LEADING));
		export.setEnabled(false);
		lowerButtons.add(export);

		exportPretty = new JCheckBox(I18N.getText("callgraph.export.pretty"));
		lowerButtons.add(exportPretty);

		JPanel graphPanel = new JPanel(new BorderLayout());

		graphFilter = new JTextField();
		graphFilter.addActionListener((ev) ->
		{
			updateGraphFilter();
		});

		graphPanel.add(graphFilter, BorderLayout.NORTH);

		callGraph = new ProfilingCallTree(decimalFormat);
		graphPanel.add(new JScrollPane(callGraph), BorderLayout.CENTER);

		showClassNames = new JCheckBox(I18N.getText("callgraph.graph.showFullClassNames"));
		lowerButtons.add(showClassNames, BorderLayout.NORTH);
		graphPanel.add(lowerButtons, BorderLayout.SOUTH);

		showClassNames.setSelected(callGraph.getShowFullClassNames());
		showClassNames.addItemListener((ev) ->
		{
			callGraph.setShowFullClassNames(showClassNames.isSelected());
		});

		add(graphPanel, BorderLayout.CENTER);
		status = new JLabel(" ");
		add(status, BorderLayout.SOUTH);

		startStopFileMonitor.addActionListener((ev) ->
		{
			if ( logStream == null )
				startFileMonitor();
			else
				stopFileMonitor(true);
		});

		fileMonitorOpen.addActionListener((ev) ->
		{
			File selectedLogFile = IOTool.selectFile(this, preference_prefix_ + "select.selectlogfile", I18N.getText("callgraph.dialog.log.open"), IOTool.OPEN, IOTool.getFileFilterLog(), null);
			if (selectedLogFile != null)
			{
				logFilePath.setText(selectedLogFile.toString());
				startFileMonitor();
			}
		});

		socketStartButton.addActionListener((ev) ->
		{
			if ( statusServiceRunner == null )
			{
				startSocketFetcher();
			}
			else
			{
				stopSocketFetcher(true);
			}
		});

		uiUpdateTimer = new Timer("UI Update");
		uiUpdateTimer.scheduleAtFixedRate(new UIUpdateTask(), 1000, 1000);

		new TextFieldSettingAdapter( statusHostInput, preference_prefix_ + "statusHost");
		new TextFieldSettingAdapter( statusPortInput, preference_prefix_ + "statusPort");
		new TextFieldSettingAdapter( logFilePath, preference_prefix_ + "logfile" );
	}

	/**
	 * Starts log file monitor.
	 */
	protected void startFileMonitor()
	{
		try
		{
		    stopSocketFetcher(false);
			stopFileMonitor(false);
			final String filePath = logFilePath.getText().trim();
			if (!filePath.isEmpty())
			{
				ticksToIdle = 9999;
				jsonParser = new JSONCallGraphParser();
				logStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath), StandardOpenOption.READ));
				Tail.addStream(logStream, this, 100, 10240);
				status.setText(status_idle);
			}
		} catch (Exception e)
		{
			logStream = null;
			status.setText(String.format(status_error, e.getClass().getSimpleName() + " - " + e.getLocalizedMessage()));
		}
		updateStatus();
	}

    /**
     * Stops log file monitor.
     */
	protected void stopFileMonitor(boolean updateUI)
	{
		try
		{
			if ( logStream != null )
			{
				Tail.removeStream( logStream, this);
				logStream.close();
				logStream = null;
				jsonParser= null;
				status.setText( status_closed );
			}
		}
		catch ( Exception e)
		{
			logStream = null;
			if ( updateUI )
			{
				status.setText( String.format(status_error, e.getClass().getSimpleName()+ " - " + e.getLocalizedMessage()));
			}
		}
		if ( updateUI )
		{
			updateStatus();
		}
	}

	private final class CallGraphConsumerImpl implements CallGraphConsumer
	{
		@Override
		public void newCallGraphs(List<JSONCallGraphParser.GraphInfo> g)
		{
			if ( !g.isEmpty() )
				callGraph.setGraph(g.get(g.size()-1));
		}

		@Override
		public void error(String message)
		{
			status.setText(String.format(status_error, message));
			stopSocketFetcher(false);
			updateStatus();
		}
	}

	CallGraphConsumerImpl consumerImpl_ = new CallGraphConsumerImpl();


    /**
     * Starts status socket monitor.
     */
    protected void startSocketFetcher()
    {
        try
        {
			stopSocketFetcher(false);
            stopFileMonitor(false);

            String socketHost = statusHostInput.getText().trim();
			String socketPort = statusPortInput.getText().trim();
            if (!socketHost.isEmpty() && !socketPort.isEmpty() )
            {
				InetSocketAddress address = new InetSocketAddress(socketHost, Integer.parseInt(socketPort));
				statusServiceRunner = new ServiceRunner(new StatusSocketService(address));
				((StatusSocketService)statusServiceRunner.getService()).addCallGraphConsumer( consumerImpl_ );
				statusServiceRunner.configureAndStart(-1, 10000);
				ticksToIdle = 9999;
				status.setText(status_idle);
			}
			else
			{
				status.setText(String.format(status_error, I18N.getText( "callgraph.socket.addressError" )));
			}
        }
        catch (NumberFormatException ne)
        {
            status.setText(String.format(status_error,  I18N.getText( "callgraph.socket.portError" )));
        }
        updateStatus();
    }


    /**
     * Stops status socket monitor.
     */
	protected void stopSocketFetcher(boolean updateUI )
    {
		if (statusServiceRunner != null)
		{
			StatusSocketService s = (StatusSocketService)statusServiceRunner.getService();
			if ( s != null )
			{
				s.removeCallGraphConsumer(consumerImpl_);
			}
			statusServiceRunner.stop();
			statusServiceRunner = null;
		}
    }

	/**
	 * Timer event without changes until "idle" status is reacjed.
	 */
	int ticksToIdle = 5;

	ServiceRunner statusServiceRunner;

	/**
	 * Background update task
	 */
	class UIUpdateTask extends TimerTask
	{

		@Override
		public void run()
		{
			if (ticksToIdle > 0)
			{
				if ((--ticksToIdle) == 0)
				{
					UITool.executeInUIThread(() ->
					{

						if (logStream == null)
						{
							status.setText(status_closed);
						} else
						{
							status.setText(status_idle);
						}
					});
				}
			}
			UITool.executeInUIThread(() ->
			{
				updateGraphFilter();
			});

		}
	}

	/**
	 * Persistence-prefix.<br>
	 * Can be modified by inheritances to make it unique.
	 */
	protected String preference_prefix_ = "ProfilingPanel.";
	protected JSONCallGraphParser jsonParser;

	/** Input field for log  file path. */
	protected JTextField logFilePath;

	/** Input fields for status url. */
	protected JTextField statusHostInput;
	protected JTextField statusPortInput;
	protected JButton socketStartButton;

	/**
	 * The status line at bottom.
	 */
	protected JLabel status;

	/**
	 * The top "start" button. Changes to "Stop" if scan was started..
	 */
	protected JButton startStopFileMonitor;
	protected Color runningBackground = new Color(180, 210, 145);


	/**
	 * The browse-button.
	 */
	protected JButton fileMonitorOpen;

	/**
	 * The export-button.
	 */
	protected JButton export;

	/**
	 * The "pretty" export option.
	 */
	protected JCheckBox exportPretty;

	/**
	 * The selected graph is shown here.
	 */
	protected ProfilingCallTree callGraph;
	/**
	 * Text box above the tree to search for nodes.
	 */
	protected JTextField graphFilter;
	protected JCheckBox showClassNames;

	/** Input Stream from log file. */
	protected InputStream logStream;

	protected String status_error;
	protected String status_read;
	protected String status_idle;
	protected String status_closed;

	/**
	 * Timer for update of the status line during scan.
	 */
	protected Timer uiUpdateTimer;

	/**
	 * In the browse-dialog selectable file-filter to find "mm" files.
	 */
	protected FileFilter freemindFileFilter;

	/**
	 * The used format to render numbers.
	 */
	protected DecimalFormat decimalFormat;

	/**
	 * Exports a graph to a Freemind file.
	 *
	 * @param exportFile The file to export to.
	 * @param graph      The graph to export.
	 */
	protected void exportFreeMind(File exportFile, JSONCallGraphParser.GraphInfo graph)
	{
		try
		{
			FreeMindGraphRenderer renderer = new FreeMindGraphRenderer(decimalFormat,
					showClassNames.isSelected() ? Options.ADD_CLASSNAMES : Options.NONE,
					Options.ADD_MIN_MAX,
					Options.HIGHLIGHT_CRITICAL,
					exportPretty.isSelected() ? Options.PRETTY : Options.NONE);
			String source = renderer.render(graph.root);
			try (FileWriter writer = new FileWriter(exportFile))
			{
				writer.write(source);
			}
			status.setText(I18N.format("callgraph.export.success", exportFile.getPath()));
		} catch (Exception e)
		{

			showException(I18N.getText("callgraph.export.error.FreeMind"), e);
		}
	}

	/**
	 * Exports a graph to a JSON file.
	 *
	 * @param exportFile The file to export to.
	 * @param graph      The graph to export.
	 */
	protected void exportJson(File exportFile, JSONCallGraphParser.GraphInfo graph)
	{
		try
		{
			JSONCallGraphRenderer renderer = new JSONCallGraphRenderer(decimalFormat, Options.ADD_CLASSNAMES, Options.ADD_MIN_MAX, Options.HIGHLIGHT_CRITICAL, exportPretty.isSelected() ? Options.PRETTY : Options.NONE);
			String source = renderer.render(graph.root);

			try (FileWriter writer = new FileWriter(exportFile))
			{
				writer.write(source);
			}
			status.setText(I18N.format("callgraph.export.success", exportFile.getPath()));
		} catch (Exception e)
		{
			showException(I18N.getText("callgraph.export.error.JSON"), e);
		}
	}

	/**
	 * Updates status and button according to the current state (scanning, idle etc.).
	 */
	protected void updateStatus()
	{
		if (logStream == null)
		{
			UIToolSwing.setI18NText(startStopFileMonitor, "callgraph.button.start");
			fileMonitorOpen.setEnabled(true);
			startStopFileMonitor.setBackground(fileMonitorOpen.getBackground());
			startStopFileMonitor.setOpaque(fileMonitorOpen.isOpaque());
			startStopFileMonitor.setBorderPainted(fileMonitorOpen.isBorderPainted());
		}
		else
		{
			UIToolSwing.setI18NText(startStopFileMonitor, "callgraph.button.stop");
			fileMonitorOpen.setEnabled(false);
			startStopFileMonitor.setBackground(runningBackground);
			startStopFileMonitor.setOpaque(true);
			startStopFileMonitor.setBorderPainted(false);

		}
		if ( statusServiceRunner == null )
		{
			UIToolSwing.setI18NText(socketStartButton, "callgraph.button.start");
			socketStartButton.setBackground(fileMonitorOpen.getBackground());
			socketStartButton.setOpaque(fileMonitorOpen.isOpaque());
			socketStartButton.setBorderPainted(fileMonitorOpen.isBorderPainted());
		}
		else
		{
			UIToolSwing.setI18NText(socketStartButton, "callgraph.button.stop");
			socketStartButton.setBackground(runningBackground);
			socketStartButton.setOpaque(true);
			socketStartButton.setBorderPainted(false);
		}
		logFilePath.setEditable(logStream == null);
	}

	/**
	 * Can be used by Application to restore last stored state from persistence.
	 *
	 * @see com.bw.jtools.persistence.Store
	 * @see #storePreferences()
	 */
	public void loadPreferences()
	{
	}

	/**
	 * Can be used by Application to store the current state to persistence.
	 *
	 * @see com.bw.jtools.persistence.Store
	 */
	public void storePreferences()
	{
	}

	/**
	 * Updates UI to show newly received graphs.
	 *
	 * @param newGraphs Number of appended graphs.
	 */
	protected void updateLogFileCallGraphs(int newGraphs)
	{
		if ( jsonParser != null )
		{
			JSONCallGraphParser.GraphInfo[] graphs = jsonParser.getCallGraphs();
			if (graphs != null)
			{
				export.setEnabled(true);
				callGraph.setGraph(graphs[graphs.length-1]);

			}
		}
	}

	/**
	 * Handles new data read from monitored file.
	 *
	 * @param data   the received data.
	 * @param offset Offset into the array.
	 * @param size   Amount of data.
	 */
	@Override
	public void read(byte[] data, int offset, int size)
	{
		if (jsonParser != null)
		{
			int oldCount = jsonParser.getNumberOfCallGraphs();
			jsonParser.parse(new String(data, offset, size, StandardCharsets.UTF_8));
			if (oldCount != jsonParser.getNumberOfCallGraphs())
			{
				updateLogFileCallGraphs(jsonParser.getNumberOfCallGraphs() - oldCount);
			}

			status.setText(String.format(status_read, (size - offset)));
			ticksToIdle = 5;
		}
	}

	protected String oldGraphFilter = null;

	protected void updateGraphFilter()
	{
		final String regExp = this.graphFilter.getText().trim();
		if (!regExp.equals(oldGraphFilter))
		{
			try
			{

				if (regExp.isEmpty())
				{
					callGraph.setNameFilter(null);
				} else
				{
					Pattern graphFilterPattern = Pattern.compile(regExp);
					callGraph.setNameFilter(graphFilterPattern);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				status.setText("RegExp Error");
			}
			oldGraphFilter = regExp;
		}
	}


	@Override
	public void exception(Throwable ex)
	{
		status.setText(String.format(status_error, ex.getMessage()));
	}

	/**
	 * Needs to be called by application on close/exit to release
	 * all threads.
	 */
	public void stop()
	{
		Tail.stop();
		if (uiUpdateTimer != null)
		{
			uiUpdateTimer.cancel();
			uiUpdateTimer = null;
		}
		stopFileMonitor( false);
		stopSocketFetcher(false);
	}

	private void showException(String message, Throwable t)
	{
		JExceptionDialog d = new JExceptionDialog(SwingUtilities.getWindowAncestor(this), null, message, t);
		d.setLocationByPlatform(true);
		d.setVisible(true);
	}

	/**
	 * Starts a standalone log scanner.
	 *
	 * @param args Command line arguments. Not used yet.
	 */
	public static void main(String args[])
	{
		Locale.setDefault(Locale.ENGLISH);

		// Initialize library.
		Application.initialize(ProfilingLogUI.class);


		// The librarie has now initialized itself from defaultsettings.properties.
		// (located in same package as the main-class).

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			Log.error("Failed to initialize LAF.", e);
		}

		ProfilingLogUI logPanel = new ProfilingLogUI();

		JFrame frame = new JFrame(I18N.getText("callgraph.frame.title"));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImages(IconTool.getAppIconImages());

		frame.setContentPane(logPanel);
		logPanel.loadPreferences();

		// It's important to register this listener before calling
		// SettingsUI.storePositionAndFlushOnClose
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				logPanel.storePreferences();

				UITool.executeInUIThread(() ->
				{
					logPanel.stop();
				});
			}

		});

		frame.pack();
		// Restore window-position and dimension from prefences.
		SettingsUI.loadWindowPosition(frame);
		SettingsUI.storePositionAndFlushOnClose(frame);

		frame.setVisible(true);

	}

}
