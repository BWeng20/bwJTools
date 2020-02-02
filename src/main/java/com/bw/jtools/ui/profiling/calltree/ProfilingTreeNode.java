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

import com.bw.jtools.profiling.callgraph.CallEdge;
import com.bw.jtools.profiling.callgraph.CallNode;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Tree node for Call Tree.
 */
public final class ProfilingTreeNode extends DefaultMutableTreeNode
{
    private static AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * Run-time unique ID of the node. Usable for maps.
     */
    public final int id;

    /**
     * The call graph node.
     */
    public CallNode node;

    /** The (bold) text to show. */
    public String text;

    /**
     * Create a new tree node.
     * @param node The call node
     * @param nf The number format used to render values.
     */
    public ProfilingTreeNode(CallNode node, NumberFormat nf)
    {
        super(node.toString(nf), !node.edges.isEmpty());
        id = idGenerator.incrementAndGet();
        this.node = node;
        for (CallEdge e : node.edges)
        {
            add(new ProfilingTreeNode(e.callee, nf));
        }
    }

}
