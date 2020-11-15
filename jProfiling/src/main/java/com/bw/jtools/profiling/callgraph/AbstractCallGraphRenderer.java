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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bw.jtools.profiling.CalleeProfilingInformation;
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfilingInformation;
import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import com.bw.jtools.profiling.measurement.DateTimeValue;
import com.bw.jtools.profiling.measurement.MeasurementValue;

/**
 * Abstract base for call graph renderer.
 */
public abstract class AbstractCallGraphRenderer {

    protected AbstractCallGraphRenderer() {
        this.nf =  NumberFormat.getInstance();
    }

    protected AbstractCallGraphRenderer(NumberFormat nf, Options... options) {
        this.nf = nf;
        for (Options option : options) {
            switch (option) {
                case ADD_CLASSNAMES:
                    showClassName = true;
                    break;
                case ADD_MIN_MAX:
                    showMinMax = true;
                    break;
                case HIGHLIGHT_CRITICAL:
                    highlightCritical = true;
                    break;
                case PRETTY:
                    pretty = true;
                    break;
                case NONE:
                    break;                    
            }
        }
    }

    protected NumberFormat nf;
    protected StringBuilder sb = new StringBuilder(1024);

    protected String renderValue(MeasurementValue value) {
        return AbstractMeasurementSource.format(nf, value);
    }

    /**
     * Renders the call graph of one method
     *
     * @param root Root node.
     * @return The call graph graphical description.
     */
    public final String render(MethodProfilingInformation root) {
        return render(generateNode(root, new GraphStack()));
    }

    /**
     * Renders a converted call graph.
     *
     * @param root Root node.
     * @return The call graph graphical description.
     */
    public final String render(CallNode root) {
        sb.setLength(0);
        start(root);
        renderNode(root);
        end(root);
        return sb.toString();
    }


    private CallNode generateNode(MethodProfilingInformation mi, GraphStack g) {
        String name;
        if (showClassName && mi.clazz != null) {
            name = mi.clazz.name + '.' + mi.name;
        } else {
            name = mi.name;
        }
        CallNode node = new CallNode(name, mi.calls, mi.sum);

        if (showMinMax) {
            node.details.add(new NodeDetail(NodeDetail.DETAIL_MINIMUM, mi.minMeasurement));
            node.details.add(new NodeDetail(NodeDetail.DETAIL_MAXIMUM, mi.maxMeasurement));
        }

        CalleeProfilingInformation highlight = null;
        if (highlightCritical) {
            MeasurementValue v = null;
            for (CalleeProfilingInformation ci : mi.callees.values()) {
                if (v == null || v.lessThan(ci.sum)) {
                    highlight = ci;
                    v = ci.sum;
                }
            }
        }
        for (CalleeProfilingInformation ci : mi.callees.values()) {
            CallNode callee;
            if (!g.onStack(ci.callee)) {
                callee = generateNode(ci.callee, g);
                g.pop(ci.callee);
            } else {
                callee = null;
            }
            CallEdge ce = new CallEdge(ci.sum, ci.calls, callee);
            ce.hightlight = (highlight == ci);
            node.edges.add(ce);
        }
        return node;
    }

    /**
     * Renders multiple calls in one graph
     *
     * @param roots     Roots of the calls.
     * @param startDate Start Date and Time of profiling.
     * @param endDate   End Date and Time of profiling.
     * @return The call graph graphical description.
     */
    public final String render(List<MethodProfilingInformation> roots, Calendar startDate, Calendar endDate) {
        CallNode fakeRoot = new CallNode("Application", 0, null);

        if (startDate != null) {
            fakeRoot.details.add(new NodeDetail(NodeDetail.DETAIL_START, new DateTimeValue(startDate)));
        }
        if (endDate != null) {
            fakeRoot.details.add(new NodeDetail(NodeDetail.DETAIL_END, new DateTimeValue(endDate)));
        }
        for (MethodProfilingInformation n : roots) {
            fakeRoot.edges.add(new CallEdge(null, 0, generateNode(n, new GraphStack())));
        }

        return render(fakeRoot);
    }

    private void renderNode(CallNode node) {
        startNode(node);
        for (CallEdge e : node.edges) {
            renderEdge(e);
        }
        endNode(node);
    }

    private void renderEdge(CallEdge edge) {
        startEdge(edge);
        renderNode(edge.callee);
        endEdge(edge);
    }

    /**
     * Option: Adds the class name to each node.
     */
    protected boolean showClassName = false;

    /**
     * Option: Highlights the critical path - if supported by renderer.
     */
    protected boolean highlightCritical = false;

    /**
     * Option: Add minimum and maximum values - if supported by renderer.
     */
    protected boolean showMinMax = false;

    /**
     * Option: Output should be easily human readable.
     */
    protected boolean pretty = false;

    /**
     * Helper class to detect recursion.
     */
    public static final class GraphStack {
        private GraphStack() {
        }

        private Set<Integer> idsOnStack = new HashSet<>();

        public boolean onStack(MethodProfilingInformation callee) {
            if (idsOnStack.contains(callee.ID)) {
                return true;
            } else {
                idsOnStack.add(callee.ID);
                return false;
            }
        }

        public void pop(MethodProfilingInformation callee) {
            idsOnStack.remove(callee.ID);
        }
    }

    /**
     * Extracts all methods that are not called by other from the list.
     *
     * @param cis Collection of class information.
     * @return The list of top-level-methods.
     */
    public static List<MethodProfilingInformation> filterTopLevelCalls(Collection<ClassProfilingInformation> cis) {
        Set<Integer> callees = new HashSet<>(cis.size() * 3);
        int count = 0;
        for (ClassProfilingInformation cli : cis) {
            List<MethodProfilingInformation> mis = cli.getMethodInformation();
            count += mis.size();
            for (MethodProfilingInformation mi : mis) {
                for (CalleeProfilingInformation ci : mi.callees.values()) {
                    callees.add(ci.callee.ID);
                }
            }
        }
        ArrayList<MethodProfilingInformation> l = new ArrayList<>(count - callees.size());
        for (ClassProfilingInformation cli : cis) {
            List<MethodProfilingInformation> mis = cli.getMethodInformation();
            for (MethodProfilingInformation mi : mis) {
                if (!callees.contains(mi.ID)) {
                    l.add(mi);
                }
            }
        }
        return l;
    }

    protected abstract void start(CallNode root);

    protected abstract void startNode(CallNode node);

    protected abstract void endNode(CallNode node);

    protected abstract void startEdge(CallEdge edge);

    protected abstract void endEdge(CallEdge edge);

    protected abstract void end(CallNode root);

}
