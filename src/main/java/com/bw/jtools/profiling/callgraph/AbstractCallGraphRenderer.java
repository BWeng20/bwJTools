/*
 * The MIT License
 *
 * Copyright 2019-2020 Bernd Wengenroth.
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
package com.bw.jtools.profiling.callgraph;

import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import com.bw.jtools.profiling.measurement.MeasurementValue;
import java.text.NumberFormat;
import java.util.List;

/**
 * Abstract base for call graph renderer.
 */
public abstract class AbstractCallGraphRenderer
{
    protected AbstractCallGraphRenderer(NumberFormat nf)
    {
        this.nf = nf;
    }

    protected NumberFormat  nf;
    protected StringBuilder sb = new StringBuilder(1024);

    protected String renderValue( MeasurementValue value)
    {
        return AbstractMeasurementSource.currentSource.format(nf, value);
    }

    /**
     * Renders the call graph of one method
     * @param root
     * @return The call graph graphical description.
     */
    public final String render( CallNode root )
    {
        sb.setLength(0);
        start( root );
        renderNode( root );
        end( root );
        return sb.toString();
    }

    /**
     * Renders multiple calls in one graph
     * @param roots
     * @return The call graph graphical description.
     */
    public final String render( List<CallNode> roots )
    {
        CallNode fakeRoot = CallGraphGenerator.generateGraph( new MethodProfilingInformation(null, "Application") );
        for ( CallNode n : roots ) {
            fakeRoot.edges.add(new CallEdge(n));
        }
        return render( fakeRoot );
    }


    private void renderNode(CallNode node)
    {
        startNode( node );
        for ( CallEdge e : node.edges )
        {
            renderEdge( e );
        }
        endNode( node );
    }

    private void renderEdge(CallEdge edge)
    {
        startEdge( edge );
        renderNode( edge.callee );
        endEdge( edge );
    }

    protected abstract void start( CallNode root );
    protected abstract void startNode( CallNode node );
    protected abstract void endNode( CallNode node );
    protected abstract void startEdge( CallEdge edge );
    protected abstract void endEdge( CallEdge edge );
    protected abstract void end( CallNode root );

}
