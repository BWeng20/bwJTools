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

import com.bw.jtools.ui.profiling.calltree.ProfilingCallTree;
import com.bw.jtools.Application;
import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.io.Tail;
import com.bw.jtools.persistence.Store;
import com.bw.jtools.profiling.callgraph.FreeMindGraphRenderer;
import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import com.bw.jtools.profiling.callgraph.JSONCallGraphRenderer;
import com.bw.jtools.profiling.callgraph.Options;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.UITool;
import com.bw.jtools.ui.UIToolSwing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

/**
 * Panel to scan logs for call graphs.
 * @see #main(java.lang.String[])
 */
public class ProfilingLogScannerUI extends JPanel implements Tail.TailListener
{
    private static boolean addI18nBundle = true;


    /**
     * Initialize a ProfilingLogViewer.
     */
    public ProfilingLogScannerUI()
    {
        if ( addI18nBundle )
        {
            addI18nBundle = false;
            I18N.addBundle("com.bw.jtools.ui.profiling.i18n", ProfilingLogScannerUI.class );
        }
        initComponents();
    }

    private void initComponents()
    {

        decimalFormat = new DecimalFormat("#.#####");

        DecimalFormatSymbols ds = decimalFormat.getDecimalFormatSymbols();
        ds.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols( ds );
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        freemindFileFilter = new FileNameExtensionFilter( I18N.getText( "callgraph.export.freemind") , "mm");

        status_error = I18N.getText("callgraph.status.error");
        status_read  = I18N.getText("callgraph.status.read");
        status_idle = I18N.getText("callgraph.status.idle");
        status_closed = I18N.getText("callgraph.status.closed");

        setLayout(new BorderLayout());

        JPanel file = new JPanel( new BorderLayout());

        fileName = new JTextField(30);
        file.add( fileName, BorderLayout.CENTER  );

        JLabel fileLabel = UIToolSwing.createI18NLabel("callgraph.log.name");
        fileLabel.setLabelFor(fileName);
        fileLabel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5));
        file.add(fileLabel, BorderLayout.LINE_START );

        JPanel buttons = new JPanel( new BorderLayout() );

        startStop = UIToolSwing.createI18NTextButton( "callgraph.button.start" );

        buttons.add( startStop, BorderLayout.LINE_START);
        open = UIToolSwing.createI18NTextButton("callgraph.button.open");
        buttons.add( open, BorderLayout.LINE_END);

        file.add( buttons, BorderLayout.LINE_END  );

        add( file, BorderLayout.NORTH );

        splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT );

        model = new ProfilingTableModel();
        JTable foundCallGraphs = new JTable(model);
        foundCallGraphs.getSelectionModel().addListSelectionListener((ListSelectionEvent ev)->
        {
            if (ev.getFirstIndex() >= 0)
            {
                export.setEnabled(true);
                callGraph.setGraph( model.getGraph(ev.getFirstIndex() ));
            }
            else
            {
                export.setEnabled(false);
            }
        });

        TableColumnModel cm = foundCallGraphs.getColumnModel();
        cm.getColumn(0).setHeaderValue(I18N.getText( "callgraph.table.root" ));
        cm.getColumn(1).setHeaderValue(I18N.getText( "callgraph.table.details" ));
        cm.getColumn(2).setHeaderValue("");

        foundCallGraphs.setFillsViewportHeight(true);

        JPanel left = new JPanel( new BorderLayout() );
        left.add( new JScrollPane(foundCallGraphs), BorderLayout.CENTER );

        JPanel leftButtons = new JPanel( new FlowLayout(FlowLayout.LEADING) );
        export = UIToolSwing.createI18NTextButton("callgraph.button.export");
        export.addActionListener((ev) ->
        {
            JSONCallGraphParser.GraphInfo graph = model.getGraph(foundCallGraphs.getSelectedRow());
            if ( graph != null )
            {

                File selectedExportFile = IOTool.selectFile( this, preference_prefix_+"select.exportfile", I18N.getText("callgraph.export.title"), IOTool.SAVE,
                         freemindFileFilter, IOTool.getFileFilterJson() );

                if ( selectedExportFile != null )
                {
                    if ( freemindFileFilter.accept(selectedExportFile) )
                    {
                        exportFreeMind( selectedExportFile, graph );
                    }
                    else if ( IOTool.getFileFilterJson().accept(selectedExportFile))
                    {
                        exportJson( selectedExportFile, graph );
                    }
                }
            }
        });
        export.setEnabled(false);
        leftButtons.add( export );

        exportPretty = new JCheckBox( I18N.getText("callgraph.export.pretty") );
        leftButtons.add( exportPretty );

        left.add(leftButtons, BorderLayout.SOUTH);

        splitter.setLeftComponent(left);

        JPanel right = new JPanel(new BorderLayout());

        graphFilter = new JTextField();
        graphFilter.addActionListener((ev) ->
        {
            updateGraphFilter();
        });

        right.add(graphFilter, BorderLayout.NORTH);

        callGraph = new ProfilingCallTree(decimalFormat);
        right.add(new JScrollPane(callGraph), BorderLayout.CENTER );

        showClassNames = new JCheckBox(I18N.getText("callgraph.graph.showFullClassNames"));
        JPanel rightButtons = new JPanel( new FlowLayout(FlowLayout.LEADING) );
        rightButtons.add( showClassNames, BorderLayout.NORTH );
        right.add(rightButtons, BorderLayout.SOUTH);

        showClassNames.setSelected( callGraph.getShowFullClassNames() );
        showClassNames.addItemListener( (ev) ->
        {
          callGraph.setShowFullClassNames( showClassNames.isSelected() );
        });

        splitter.setRightComponent(right);

        add(splitter, BorderLayout.CENTER  );
        status = new JLabel(" ");
        add( status, BorderLayout.SOUTH );

        startStop.addActionListener((ev) -> {
            try
            {
                final String filePath = fileName.getText().trim();
                if ( !filePath.isEmpty() )
                {

                    if ( logStream != null )
                    {
                        Tail.removeStream( logStream, this);
                        logStream.close();
                        logStream = null;
                        jsonParser= null;
                        status.setText( status_closed );
                    }
                    else
                    {
                        ticksToIdle = 9999;
                        jsonParser= new JSONCallGraphParser();
                        logStream = new BufferedInputStream( Files.newInputStream( Paths.get(filePath), StandardOpenOption.READ ));
                        Tail.addStream( logStream, this, 100, 10240 );
                        status.setText(status_idle);
                    }
                }
            }
            catch ( Exception e)
            {
                logStream = null;
                status.setText( String.format(status_error, e.getClass().getSimpleName()+ " - " + e.getLocalizedMessage()));
            }
            updateStatus();
        });

        open.addActionListener((ev) -> {

            File selectedLogFile = IOTool.selectFile( this, preference_prefix_+"select.selectlogfile", I18N.getText("callgraph.dialog.log.open"), IOTool.OPEN,  IOTool.getFileFilterLog(), null);
            if ( selectedLogFile != null )
            {
                fileName.setText( selectedLogFile.toString() );
            }
        });

        uiUpdateTimer = new Timer("UI Update");
        uiUpdateTimer.scheduleAtFixedRate( new UIUpdateTask(), 1000, 1000 );
    }

    int ticksToIdle = 5;

    class UIUpdateTask extends TimerTask
    {

        @Override
        public void run()
        {
            if ( ticksToIdle>0 )
            {
                if ( (--ticksToIdle) == 0)
                {
                    UITool.executeInUIThread(()->{

                        if ( logStream == null )
                        {
                            status.setText( status_closed );
                        }
                        else
                        {
                            status.setText( status_idle );
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

    protected JTextField fileName;
    protected ProfilingTableModel model;

    /** The status line at bottom. */
    protected JLabel status;

    /** The main split area, separates table of found graphs and the tree. */
    protected JSplitPane splitter;

    /** The top "start" button. Changes to "Stop" if scan was started.. */
    protected JButton startStop;
    protected Color runningBackground = new Color( 180,210,145 );


    /** The browse-button. */
    protected JButton open;

    /** The export-button. */
    protected JButton export;

    /** The "pretty" export option. */
    protected JCheckBox exportPretty;

    /** The selected graph is shown here. */
    protected ProfilingCallTree callGraph;
    /** Text box above the tree to search for nodes. */
    protected JTextField  graphFilter;
    protected JCheckBox showClassNames;

    protected InputStream logStream;

    protected String status_error;
    protected String status_read;
    protected String status_idle;
    protected String status_closed;

    /** Timer for update of the status line during scan. */
    protected Timer uiUpdateTimer;

    /** In the browse-dialog selectable file-filter to find "mm" files. */
    protected FileFilter freemindFileFilter;

    /** The used format to render numbers. */
    protected DecimalFormat decimalFormat;

    /**
     * Exports a graph to a Freemind file.
     * @param exportFile The file to export to.
     * @param graph The graph to export.
     */
    protected void exportFreeMind( File exportFile, JSONCallGraphParser.GraphInfo graph )
    {
        try
        {
            FreeMindGraphRenderer renderer = new FreeMindGraphRenderer(decimalFormat, Options.ADD_CLASSNAMES, Options.ADD_MIN_MAX, Options.HIGHLIGHT_CRITICAL, exportPretty.isSelected() ? Options.PRETTY : Options.NONE );
            String source = renderer.render( graph.root );
            try ( FileWriter writer = new FileWriter(exportFile) )
            {
                writer.write(source);
            }
            status.setText( I18N.format("callgraph.export.success", exportFile.getPath() ));
        }
        catch ( Exception e)
        {

            showException( I18N.getText("callgraph.export.error.FreeMind" ), e );
        }
    }

    /**
     * Exports a graph to a JSON file.
     * @param exportFile The file to export to.
     * @param graph The graph to export.
     */
    protected void exportJson( File exportFile, JSONCallGraphParser.GraphInfo graph )
    {
        try
        {
            JSONCallGraphRenderer renderer = new JSONCallGraphRenderer(decimalFormat, Options.ADD_CLASSNAMES, Options.ADD_MIN_MAX, Options.HIGHLIGHT_CRITICAL, exportPretty.isSelected() ? Options.PRETTY : Options.NONE );
            String source = renderer.render( graph.root );

            try ( FileWriter writer = new FileWriter(exportFile) )
            {
                writer.write(source);
            }
            status.setText( I18N.format("callgraph.export.success", exportFile.getPath() ));
        }
        catch ( Exception e)
        {
            showException( I18N.getText("callgraph.export.error.JSON" ), e );
        }
    }

    /**
     * Updates status and button according to the current state (scanning, idle etc.).
     */
    protected void updateStatus()
    {
        if ( logStream == null )
        {
            UIToolSwing.setI18NText(startStop, "callgraph.button.start");
            open.setEnabled(true);
            startStop.setBackground( open.getBackground() );
            startStop.setOpaque(open.isOpaque());
            startStop.setBorderPainted(open.isBorderPainted());
        }
        else
        {
            UIToolSwing.setI18NText(startStop, "callgraph.button.stop");

            open.setEnabled(false);

            startStop.setBackground( runningBackground);
            startStop.setOpaque(true);
            startStop.setBorderPainted(false);
        }
        fileName.setEditable(logStream == null);
    }

    /**
     * Can be used by Application to restore last stored state from persistence.
     *
     * @see com.bw.jtools.persistence.Store
     * @see #storePreferences()
     */
    public void loadPreferences()
    {
        final String lf = Store.getString(preference_prefix_+"logfile", null );
        if ( lf != null && !lf.isEmpty())
        {
            fileName.setText( lf );
            startStop.setEnabled(true);
        }
        int splitPos = Store.getInt(preference_prefix_+"splitpos", -1);
        if (splitPos > 0)
        {
            splitter.setDividerLocation(splitPos);
        }

    }

    /**
     * Can be used by Application to store the current state to persistence.
     *
     * @see com.bw.jtools.persistence.Store
     */
    public void storePreferences()
    {
        Store.setString(preference_prefix_+"logfile", fileName.getText().trim() );
        Store.setInt(preference_prefix_+"splitpos", splitter.getDividerLocation() );

    }

    /**
     * Updates UI to show newly received graphs.
     * @param newGraphs Number of appended graphs.
     */
    protected void updateCallGraphs(int newGraphs )
    {
        JSONCallGraphParser.GraphInfo[] graphs = jsonParser.getCallGraphs();

        if( graphs != null )
        {
            for (int i = graphs.length-newGraphs; i<graphs.length; ++i )
            {
                model.addGraph( graphs[i] );
            }
        }
        else
        {
            model.clear();
        }
    }

    @Override
    public void read(byte[] data, int offset, int size)
    {
        if ( jsonParser != null )
        {
            int oldCount = jsonParser.getNumberOfCallGraphs();
            jsonParser.parse( new String(data, offset, size, StandardCharsets.UTF_8 ) );
            if ( oldCount != jsonParser.getNumberOfCallGraphs() )
            {
                updateCallGraphs(jsonParser.getNumberOfCallGraphs()-oldCount);
            }

            status.setText( String.format( status_read, (size-offset)) );
            ticksToIdle = 5;
        }
        else
        {
        }
    }

    protected String oldGraphFilter = null;

    protected void updateGraphFilter()
    {
        final String regExp = this.graphFilter.getText().trim();
        if ( !regExp.equals( oldGraphFilter ) )
        {
            try
            {

                if ( regExp.isEmpty() ) {
                   callGraph.setNameFilter(null);
                }
                else
                {
                    Pattern graphFilterPattern = Pattern.compile(regExp);
                    callGraph.setNameFilter( graphFilterPattern  );
                }
            }
            catch ( Exception e)
            {
                e.printStackTrace();
                status.setText( "RegExp Error" );
            }
            oldGraphFilter = regExp;
        }
    }


    @Override
    public void exception(Throwable ex)
    {
        status.setText( String.format( status_error, ex.getMessage()));
    }

    /**
     * Needs to be called by application on close/exit to release
     * all threads.
     */
    public void stop()
    {
        Tail.stop();
        if ( uiUpdateTimer != null )
        {
            uiUpdateTimer.cancel();
            uiUpdateTimer = null;
        }
    }

    private void showException( String message, Throwable t )
    {
        JExceptionDialog d = new JExceptionDialog( SwingUtilities.getWindowAncestor(this), null, message, t );
        d.setLocationByPlatform(true);
        d.setVisible(true);
    }

    /**
     * Starts a standalone log scanner.
     * @param args Command line arguments. Not used yet.
     */
    public static void main( String args[] )
    {
        Locale.setDefault(Locale.ENGLISH);

        // Initialize library.
        Application.initialize(ProfilingLogScannerUI.class);


        // The librarie has now initialized itself from defaultsettings.properties.
        // (located in same package as the main-class).

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            Log.error("Failed to initialize LAF.", e);
        }

        ProfilingLogScannerUI logPanel = new ProfilingLogScannerUI();

        JFrame frame = new JFrame( I18N.getText("callgraph.frame.title") );
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages( IconCache.getAppIconImages() );

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
        SettingsUI.storePositionAndFlushOnClose( frame );

        frame.setVisible(true);

    }

}
