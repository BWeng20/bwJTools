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
import javax.swing.JTree;

/**
 * Component to show call graphs.
 * @see #main(java.lang.String[])
 */
public class ProfilingCallGraph extends JTree
{
    /**
     * Initialize the panel.
     */
    public ProfilingCallGraph()
    {
        super(new ProfilingTreeModel(null));
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

    public void setGraph(JSONCallGraphParser.GraphInfo graph)
    {
        this.graph = graph;
        ProfilingTreeModel model = new ProfilingTreeModel(graph);
        setModel( model );
    }

    JSONCallGraphParser.GraphInfo graph;


}
