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

import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * Table Model for the list of found Call Graphs.
 */
public class ProfilingTableModel extends AbstractTableModel
{
    /**
	 *  Generated Serial Version
	 */
	private static final long serialVersionUID = 8616212306085967142L;

	private String[] columnNames = { "Root", "Details", "" };
    private ArrayList<JSONCallGraphParser.GraphInfo> graphs = new ArrayList<>();

    /**
     * Adds a graph.
     * @param g The Graph to add.
     */
    public void addGraph( JSONCallGraphParser.GraphInfo g )
    {
        graphs.add(g);
        fireTableRowsInserted(graphs.size()-1,graphs.size()-1);
    }

    /**
     * Gets a graph by index.
     * @param index The index of the graph to get. Have to be in [0..{@link #getRowCount()}-1]:
     * @return The graph or null.
     */
    public JSONCallGraphParser.GraphInfo getGraph( int index )
    {
        if ( index >= 0 && index < graphs.size())
        {
            return graphs.get(index);
        }
        return null;
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return graphs.size();
    }

    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    private final static String EMPTY = "Empty";
    private final static String NULL = "";

    public Object getValueAt(int row, int col)
    {
        if ( graphs.size() > row && col < 3)
        {
            JSONCallGraphParser.GraphInfo g = graphs.get(row);
            switch ( col )
            {
                case 0: return (g.root!=null) ? g.root.name : EMPTY;
                case 1:
                    if (g.root!=null && g.root.details != null && g.root.details.size()>0)
                    {
                         return g.root.details.get(0);
                    }
                    break;
                case 2:
                    if (g.root!=null && g.root.details != null && g.root.details.size()>1)
                    {
                         return g.root.details.get(1);
                    }
                    break;
            }
        }
        return NULL;
    }

    public Class<?> getColumnClass(int c)
    {
        return String.class;
    }

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    public void setValueAt(Object value, int row, int col)
    {
    }

    void clear()
    {
        graphs.clear();
        fireTableDataChanged();
    }

}
