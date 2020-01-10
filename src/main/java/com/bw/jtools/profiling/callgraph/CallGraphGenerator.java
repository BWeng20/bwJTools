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
import com.bw.jtools.profiling.ClassProfilingInformation;
import com.bw.jtools.profiling.MethodProfilingInformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transforms the profiling information into a directed acyclic graph (DAC).
 */
public final class CallGraphGenerator
{

    /**
     * Generates a call graph.
     * @param mi The method to use as root.
     * @return The root of the call-graph.
     */
    public static CallNode generateGraph( MethodProfilingInformation mi, Options options )
    {
        return new CallNode(mi, new GraphStack(), options);
    }

    /**
     * Generates a call graph with default options.
     * @param mi The method to use as root.
     * @return The root of the call-graph.
     */
    public static CallNode generateGraph( MethodProfilingInformation mi )
    {
        return generateGraph(mi, DEFAULT_OPTIONS );
    }


    /**
     * Extracts all methods that are not called by other from the list.
     * @param cis Collection of class information.
     * @return The list of top-level-methods.
     */
    public static List<MethodProfilingInformation> filterTopLevelCalls(Collection<ClassProfilingInformation> cis)
    {
        Set<Integer> callees = new HashSet<>(cis.size() * 3);
        int count = 0;
        for (ClassProfilingInformation cli : cis)
        {
            List<MethodProfilingInformation> mis = cli.getMethodInformation();
            count += mis.size();
            for (MethodProfilingInformation mi : mis)
            {
                for (CalleeProfilingInformation ci : mi.callees.values())
                {
                    callees.add(ci.callee.ID);
                }
            }
        }
        ArrayList<MethodProfilingInformation> l = new ArrayList<>(count - callees.size());
        for (ClassProfilingInformation cli : cis)
        {
            List<MethodProfilingInformation> mis = cli.getMethodInformation();
            for (MethodProfilingInformation mi : mis)
            {
                if (!callees.contains(mi.ID))
                {
                    l.add(mi);
                }
            }
        }
        return l;
    }

    /**
     * Options.
     */
    public static class Options
    {
        /** Adds the class name to each node. */
        public boolean showClassName = true;

        /** Highlights the critical path - if supported by renderer. */
        public boolean hightlightCritical = true;
    }

    /**
     * Default options.
     */
    public static final Options DEFAULT_OPTIONS = new Options();

    /**
     * Helper class to detect recursion.
     */
    public static final class GraphStack
    {
        private GraphStack()
        {
        }

        private Set<Integer> idsOnStack = new HashSet<>();

        public boolean onStack(MethodProfilingInformation callee)
        {
            if ( idsOnStack.contains(callee.ID))
            {
                return true;
            } else {
                idsOnStack.add(callee.ID);
                return false;
            }
        }

        public void pop(MethodProfilingInformation callee)
        {
            idsOnStack.remove(callee.ID);
        }
    }

}
