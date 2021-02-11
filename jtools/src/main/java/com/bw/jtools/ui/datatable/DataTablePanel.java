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
package com.bw.jtools.ui.datatable;

import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.persistence.Store;
import com.bw.jtools.ui.*;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel to show a Data Table plus filter and a context-menu.<br>
 * If Apache CSV (<i>org.apache.commons.csv</i>) is present in the class-path, the context menu will
 * also contain a CSV-Export option.<br>
 * This class can also be used as copy&amp;paste-template for more custom use-cases.
 */
public final class DataTablePanel extends javax.swing.JPanel
{
    /**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 8742358607589881044L;

	boolean auto_scroll_ = false;

    /**
     * Scrolls to end of list.
     */
    public void scroll2End()
    {
        if (auto_scroll_ && view_ != null && false == view_.hasFocus())
        {
            view_.scrollRectToVisible(view_.getCellRect(view_.getRowCount() - 1, 0, true));
        }
    }

    protected String preference_prefix_ = "LogPanel.";

    /**
     * In case the application needs multiple instances of this class that
     * stores their preferences, this method can be used to assign a unique
     * prefix.<br>
     * Default is "LogPanel.".
     *
     * @param prefix New prefix or null. Null restores default.
     */
    public void setPreferencePrefix(String prefix)
    {
        if (prefix == null)
        {
            preference_prefix_ = "LogPanel.";
        } else
        {
            preference_prefix_ = prefix;
        }
        if (view_ != null)
        {
            view_.setPreferencePrefix(prefix);
        }
    }

    /**
     * Can be used by Application to restore last stored state from persistence.
     *
     * @see com.bw.jtools.persistence.Store
     */
    public void loadPreferences()
    {
        auto_scroll_ = Store.getBoolean( preference_prefix_+"autoscroll", false);
        auto_scroll_check_.setSelected(auto_scroll_);
        regexp_check_.setSelected(Store.getBoolean( preference_prefix_+"filter.regexp", false));
        search_text_.setText(Store.getString( preference_prefix_+"filter.value", ""));
        if (view_ != null)
        {
            view_.loadPreferences();
        }
    }

    /**
     * Can be used by Application to store state to persistence.
     *
     * @see com.bw.jtools.persistence.Store
     */
    public void storePreferences()
    {
        Store.setBoolean( preference_prefix_+"autoscroll", auto_scroll_);
        Store.setBoolean( preference_prefix_+"filter.regexp", regexp_check_.isSelected());
        Store.setString( preference_prefix_+"filter.value", search_text_.getText());
        if (view_ != null)
        {
            view_.storePreferences();
        }
    }

    /**
     * Creates a new DataTable-Panel.
     * @param model The model.
     */
    public DataTablePanel(DataTableModel model )
    {
        super( new BorderLayout() );

        initComponents(model);

        view_.setPreferencePrefix(preference_prefix_);

        auto_scroll_ = true;
        auto_scroll_check_.setSelected(auto_scroll_);

        Object[][] contextMenuEntries = new Object[][]
                {
                    { I18N.getText("logpanel.clear" ), IconTool.getIcon(DataTablePanel.class, "edit-clear.png" )},
                    { I18N.getText("logpanel.export"), IconTool.getIcon(DataTablePanel.class, "media-floppy.png")},
                };
        try
        {
            csvExport = new DataCSVExporter();
        }
        catch ( java.lang.NoClassDefFoundError t )
        {
            Log.debug("CSV support not available.", t );
            contextMenuEntries[1] = null;
        }
        new JContextMenu(
                "LogPanel", view_,
                contextMenuEntries,
                new Runnable[]
                {
                    () -> ((DataTableModel) view_.getModel()).setRowCount(0),
                    this::storeLog
                });

        updateFilter();

    }

    private void initComponents(DataTableModel model)
    {
        filter_panel_ = new JPanel( new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        search_text_ = new JTextField();
        search_text_.addActionListener((java.awt.event.ActionEvent evt) -> updateFilter());

        gc.gridx=GridBagConstraints.LINE_START;
        gc.gridy=0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.BASELINE_LEADING;
        filter_panel_.add( search_text_, gc );

        regexp_check_ = new JCheckBox();
        regexp_check_.setMnemonic('R');
        regexp_check_.setText( I18N.getText("logpanel.regexp.check"));
        regexp_check_.addActionListener((java.awt.event.ActionEvent evt) -> updateFilter());
        gc.gridx=GridBagConstraints.RELATIVE;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        filter_panel_.add( regexp_check_, gc );

        regexp_help_ = new JLink();
        regexp_help_.setAlias( I18N.getText("logpanel.regexp.help") );
        regexp_help_.setUri("https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html");
        gc.ipadx = 10;
        filter_panel_.add( regexp_help_, gc);
        gc.ipadx = 0;

        if ( model instanceof LoggerDataModel )
        {
            JPopupMenu levelMenu = new JPopupMenu( I18N.getText("logpanel.levelfilter") );
            levelMenu.add( debugCheck_ = new JCheckBox( I18N.getText("logpanel.debug")) );
            levelMenu.add( infoCheck_ = new JCheckBox( I18N.getText("logpanel.info")) );
            levelMenu.add( warningCheck_ = new JCheckBox( I18N.getText("logpanel.warning")) );
            levelMenu.add( errorCheck_ = new JCheckBox( I18N.getText("logpanel.error")) );

            debugCheck_.setSelected(true);
            infoCheck_ .setSelected(true);
            warningCheck_ .setSelected(true);
            errorCheck_ .setSelected(true);

            ItemListener lvlIL = (ItemEvent evt) -> updateFilter();

            debugCheck_.addItemListener(lvlIL);
            infoCheck_ .addItemListener(lvlIL);
            warningCheck_ .addItemListener(lvlIL);
            errorCheck_ .addItemListener(lvlIL);

            JMenuButton levelFilterButton = new JMenuButton(I18N.getText("logpanel.levelfilter"), null, levelMenu );
            levelFilterButton.setBorder(BorderFactory.createEmptyBorder(2,4,2,4));
            filter_panel_.add( levelFilterButton, gc);
        }

        auto_scroll_check_ = new JCheckBox( I18N.getText("logpanel.autoscroll") );
        auto_scroll_check_.addItemListener(this::auto_scroll_check_ItemStateChanged);
        filter_panel_.add( auto_scroll_check_, gc);

        add(filter_panel_, java.awt.BorderLayout.NORTH);

        view_ = new DataTable(model);
        table_scoller_ = new JScrollPane();
        table_scoller_.setViewportView(view_);
        add(table_scoller_, java.awt.BorderLayout.CENTER);

        setPreferredSize(new Dimension(600, 450));
    }

    private boolean scrollListener_invoked_ = false;

    private TableModelListener scrollListener_ = (TableModelEvent e) ->
    {
        if ( e.getType() == TableModelEvent.INSERT && !scrollListener_invoked_)
        {
            scrollListener_invoked_ = true;
            UITool.executeInUIThread(() ->
            {
                scrollListener_invoked_ = false;
                scroll2End();
            });
        }
    };

    private void auto_scroll_check_ItemStateChanged(@SuppressWarnings("unused") java.awt.event.ItemEvent evt)
    {
        auto_scroll_ = auto_scroll_check_.isSelected();

        DataTableModel model = (DataTableModel) view_.getModel();
        model.removeTableModelListener(scrollListener_);
        if ( auto_scroll_ )
        {
            model.addTableModelListener(scrollListener_);
            scroll2End();
        }
    }

    /**
     * Exports the log as CSV.<br>
     * Call will be ignored if Apache CSV if
     */
    public void storeLog()
    {
        if ( csvExport != null )
        {
            File file = IOTool.selectFile(this, preference_prefix_+"export", I18N.getText( "logpanel.exportcsv.title"), IOTool.SAVE,
                    new FileNameExtensionFilter( I18N.getText( "logpanel.exportcsv.csvfile") , "csv"));
            if (file != null)
            {
                try
                {
                    csvExport.export( file, (LoggerDataModel)view_.getModel() );
                }
                catch (Exception ex)
                {
                    Log.error(ex.getLocalizedMessage(),ex);
                }
            }
        }
    }

    protected void updateFilter()
    {
        final String filter_text = search_text_.getText();
        final boolean filter_regExp = regexp_check_.isSelected();

        List<RowFilter<DataTableModel,Integer>> filter = new ArrayList<>();

        if (!filter_text.isEmpty())
        {
            try
            {
                if (filter_regExp)
                {
                    filter.add( RowFilter.regexFilter(filter_text));
                }
                else
                {
                    filter.add( new StringRowFilter(filter_text));
                }
                if (search_text_ != null && error_color_ != null)
                {
                    search_text_.setBackground(getBackground());
                }
            }
            catch (Exception e)
            {
                Log.error(e.getMessage());
                filter.clear();
                if (search_text_ != null && error_color_ != null)
                {
                    search_text_.setBackground(error_color_);
                }
            }
        }

        if ( debugCheck_ != null )
        {
            boolean debug = debugCheck_.isSelected();
            boolean info = infoCheck_.isSelected();
            boolean warning = warningCheck_.isSelected();
            boolean error = errorCheck_.isSelected();

            if ( !(debug && info && warning && error) )
            {
                boolean[] levelFlags = new boolean[Log.DEBUG+1];
                levelFlags[ Log.ERROR ] = error;
                levelFlags[ Log.WARN  ] = warning;
                levelFlags[ Log.INFO  ] = info;
                levelFlags[ Log.DEBUG  ]= debug;

                filter.add( new LogLevelRowFilter( levelFlags ) );
            }
        }

        view_.setFilters( filter );
    }

    private javax.swing.JCheckBox auto_scroll_check_;
    private javax.swing.JPanel filter_panel_;
    private javax.swing.JCheckBox regexp_check_;
    private JLink regexp_help_;
    private javax.swing.JTextField search_text_;
    private javax.swing.JScrollPane table_scoller_;
    private DataTable view_;

    private JCheckBox debugCheck_;
    private JCheckBox infoCheck_;
    private JCheckBox warningCheck_;
    private JCheckBox errorCheck_;

    private IDataExporter csvExport;

    protected Color error_color_ = new Color(250, 200, 200);


}
