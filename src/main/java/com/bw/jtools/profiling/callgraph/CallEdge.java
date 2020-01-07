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

import com.bw.jtools.profiling.CalleeProfilingInformation;
import com.bw.jtools.profiling.measurement.MeasurementValue;

/**
 * A directed edge inside a call graph.
 */
public final class CallEdge
{
    public CallEdge( CallNode node )
    {
        callee = node;
        value = null;
        count = node.calls;
    }

    public CallEdge( CalleeProfilingInformation ci, CallGraphGenerator.GraphStack g )
    {
        if ( !g.onStack(ci.callee) ) {
            callee = new CallNode(ci.callee, g);
            g.pop(ci.callee);
        }
        else {
            callee = null;
        }
        value = ci.sum;
        count = ci.calls;
    }

    /**
     * The Callee.
     */
    public final CallNode callee;

    /**
     * The measurement value of the callee-relation.
     */
    public final MeasurementValue value;

    public final int count;
}
