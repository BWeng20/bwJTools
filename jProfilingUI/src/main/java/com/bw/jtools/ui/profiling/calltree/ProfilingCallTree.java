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
package com.bw.jtools.ui.profiling.calltree;

import com.bw.jtools.profiling.callgraph.JSONCallGraphParser;

import javax.swing.JTree;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * Component to show call graphs.
 */
public class ProfilingCallTree extends JTree
{
    /**
	 *  Generated Serial Version
	 */
	private static final long serialVersionUID = 4279375455458945581L;
	
	protected JSONCallGraphParser.GraphInfo graph;
    protected NumberFormat nf;

    /**
     * Initialize the panel.
     * @param nf The number-format to use.
     */
    public ProfilingCallTree(NumberFormat nf )
    {
        super(new ProfilingCallTreeModel(null));
        this.nf = nf;
        init();
    }

    protected final void init()
    {
        setCellRenderer(new ProfilingCallTreeRenderer(nf));
        setRowHeight(0);
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
     * Sets the shown graph.
     * @param graph The graph to show.
     */
    public void setGraph(JSONCallGraphParser.GraphInfo graph)
    {
        ProfilingCallTreeModel oldmodel = (ProfilingCallTreeModel)getModel();
        this.graph = graph;
        ProfilingCallTreeModel model = new ProfilingCallTreeModel(graph);
        model.setNameFilter(oldmodel.getNameFilter());
        model.setShowFullClassNames(oldmodel.getShowFullClassNames());
        setModel( model );
    }

    /**
     * gets the shown graph.
     * @return  The current graph or null.
     */
    public JSONCallGraphParser.GraphInfo getGraph()
    {
        return graph;
    }

    /**
     * Sets display mode for classes.
     * @param sff If true class-names are printed with package prefix.
     */
    public void setShowFullClassNames( boolean sff )
    {
        ProfilingCallTreeModel model = (ProfilingCallTreeModel)getModel();
        if (model != null)
        {
            model.setShowFullClassNames(sff);
        }
    }

    public boolean getShowFullClassNames()
    {
        ProfilingCallTreeModel model = (ProfilingCallTreeModel)getModel();
        if (model != null)
        {
            return model.getShowFullClassNames();
        }
        return false;
    }

    public void setNameFilter(Pattern pattern)
    {
        ProfilingCallTreeModel model = (ProfilingCallTreeModel)getModel();
        if (model != null)
        {
            model.setNameFilter(pattern);
        }
    }


}
