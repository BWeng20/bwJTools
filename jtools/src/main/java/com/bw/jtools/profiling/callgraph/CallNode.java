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

import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import com.bw.jtools.profiling.measurement.MeasurementValue;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A node inside a call graph.
 */
public final class CallNode
{
    /**
     * Create a new Node for a method.<br>
     * Remind that a method can have multiple nodes inside the graph.
     * @param name  The name.
     * @param calls Absolute number of calls.
     * @param value The measured value.
     */
    public CallNode( String name, int calls, MeasurementValue value ) {
        this.name    = name;
        this.calls   = calls;
        this.value   = (value == null)?null:value.clone();
        this.details = new ArrayList<>();
        this.edges   = new ArrayList<>();
    }

    /**
     * Gets the string representation of the value.
     * @param nf The format to use to render the numbers.
     * @return The resulting string.
     */
    public String toString(NumberFormat nf)
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append( name );
        if ( value != null )
        {
            sb
                    .append( ' ' )
                    .append( AbstractMeasurementSource.format( nf, value) )
                    .append( ", net " )
                    .append( AbstractMeasurementSource.format( nf, getNetMeasurement() ) );
        }
        return sb.toString();
    }

    /**
     * The name of the method/code-unit
     */
    public final String name;

    /**
     * Additional details to show.<br>
     * Can be empty or null.
     */
    public List<String> details;

    /**
     * The absolute number of calls to the method.
     */
    public final int calls;

    /**
     * The measurement value.
     */
    public final MeasurementValue value;

    /**
     * The net measurement value (value minus sum of edges)
     */
    public MeasurementValue netValue;

    /**
     * The edges to called methods.
     */
    public final List<CallEdge> edges;

    /**
     * Get the used net value.<br>
     * Net value is the value minus the sum of all callees.
     * @return The net measurement value.
     */
    public final MeasurementValue getNetMeasurement()
    {
        if ( netValue == null  && value != null)
        {
            MeasurementValue net = value.clone();
            for (CallEdge e : edges)
            {
                net.subtract(e.value);
            }
            netValue = net;
        }
        return netValue;
    }

}
