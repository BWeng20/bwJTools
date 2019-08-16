package com.bw.jtools.ui.data;

import javax.swing.RowFilter;

/**
 * Simple textual filter for Data Table.<br>
 * Filters rows which contain the specified sub-string.
 */
class StringRowFilter extends RowFilter<DataTableModel, Integer>
{

    /**
     * Creates a new filter.
     */
    StringRowFilter( String val )
    {
        value_ = val;
    }

    String value_;

    @Override
    public boolean include(Entry<? extends DataTableModel, ? extends Integer> entry)
    {
        final int N = entry.getValueCount();
        for ( int i = 0 ; i<N; ++i )
            if ( entry.getStringValue(i).contains(value_) ) return true;
        return false;
    }

}
