package com.bw.jtools.ui.data;

import com.bw.jtools.persistence.Store;
import java.awt.Color;
import java.util.List;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 * Table to show generic data.
 * @see DataTableModel
 */
public final class DataTable extends JTable
{
    /**
     * Sorter instance to be used for sorting.
     */
    protected TableRowSorter<DataTableModel> sorter_;

    /**
     * Sorter instance to be used for sorting.
     */
    protected JViewport viewport_;

    /**
     * The Scrollpane around the text area.
     */
    protected JScrollPane scrollpane_;

    /**
     * Get "editable" state.
     *
     * @return Current editable state.
     */
    public boolean isEditable()
    {
        return editable_;
    }

    /**
     * Set "editable" state.<br>
     *
     * @param enable New editable state.
     */
    public void setEditable(boolean enable)
    {
        editable_ = enable;
    }

    private boolean editable_;

    /**
     * Create a new DataTable instance.
     */
    public DataTable(DataTableModel mdl)
    {
        editable_ = false;

        setDoubleBuffered(true);
        setEditable(false);
        setModel(mdl);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        setAutoscrolls(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // To solve all inconsistences accross all LookAndFeels, set the background to a fixed value:
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
        defaults.put("Table.background", Color.WHITE);

        TableColumnModel colModel = getColumnModel();
        sorter_ = new TableRowSorter<>(mdl);

        final int CN = colModel.getColumnCount();

        for (int cIdx = 0; cIdx < CN; ++cIdx)
        {
            sorter_.setSortable(cIdx, false);
            colModel.getColumn(cIdx).setCellRenderer( mdl.getCellRenderer(cIdx) );
        }
        setRowSorter(sorter_);


        // Setup copy & paste support
        InputMap imap = this.getInputMap();
        imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
    }

    protected String preference_prefix_ = "LogTable.";


    /**
     * In case the application needs multiple instances of this class that
     * stores their preferences, this method can be used to assign a unique
     * prefix.<br>
     * Default is "LogTable.".
     *
     * @param prefix New prefix or null. Null restores default.
     */
    public void setPreferencePrefix(String prefix)
    {
        if (prefix == null)
        {
            preference_prefix_ = "LogTable.";
        } else
        {
            preference_prefix_ = prefix;
        }
    }

    /**
     * Can be used by Application to restore last stored state of columns from
     * persistence.
     *
     * @see com.bw.jtools.persistence.Store
     */
    public void loadPreferences()
    {
        final int N = getColumnCount();
        TableColumnModel colModel = getColumnModel();
        for (int c = 0; c < N; ++c)
        {
            TableColumn tc = colModel.getColumn(c);
            int colWidth = Store.getInt(preference_prefix_ + "col" + (c + 1) + ".width", 80 );
            if (colWidth > 0)
            {
                tc.setPreferredWidth(colWidth);
            }
        }
        doLayout();

    }

    /**
     * Can be used by Application to store state of columns to persistence.
     *
     * @see com.bw.jtools.persistence.Store
     */
    public void storePreferences()
    {
        final int N = getColumnCount();
        TableColumnModel colModel = getColumnModel();

        for (int c = 0; c < N; ++c)
        {
            int colWidth = colModel.getColumn(c).getWidth();
            Store.setString(preference_prefix_ + "col" + (c + 1) + ".width", String.valueOf(colWidth));
        }
    }

    /**
     * Sets Filters.<br>
     * Filters are combined with "and".
     *
     * @param filters The new list of filters.
     */
    public void setFilters(List<RowFilter<DataTableModel,Integer>> filters)
    {
        if ( filters != null && !filters.isEmpty() )
        {
            sorter_.setRowFilter( RowFilter.andFilter(filters) );
        }
        else
        {
            sorter_.setRowFilter(null);
        }

    }


}
