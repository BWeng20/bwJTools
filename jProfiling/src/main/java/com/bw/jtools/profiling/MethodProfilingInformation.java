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
package com.bw.jtools.profiling;

import com.bw.jtools.profiling.measurement.MeasurementValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds profiling information about a method or other code-unit during run-time.
 */
public final class MethodProfilingInformation extends IdentifiableProfilingInformation
{

    /**
     * The method/code unit name.
     */
    public final String name;

    /**
     * The class/module of this method/code-unit.
     */
    public final ClassProfilingInformation clazz;

    /**
     * Number of exceptions of this method.
     */
    public int exceptions = 0;

    /**
     * Callees from this method.<br>
     * Methods that are profiled during a call of this method are collected here.<br>
     * Remind that only profiled methods are collected. Relation is not transitive.
     */
    public final Map<Integer, CalleeProfilingInformation> callees;

    /**
     * C'tor to create a new Method-Information instance.
     * @param clazz The class information the method belongs to.
     * @param name The name of the method.
     */
    public MethodProfilingInformation(final ClassProfilingInformation clazz, final String name)
    {
        this.callees = new HashMap<>(13);
        this.clazz = clazz;
        this.name = name;
    }

    /**
     * Get the callee instance for the given method.<br>
     * If not callee instance exists a new one is created and added.
     *
     * @param mi The method for which the callee-information should be returned.
     * @return The callee information, never null.
     */
    public CalleeProfilingInformation getCalleeInformation(MethodProfilingInformation mi)
    {
        synchronized (callees)
        {
            CalleeProfilingInformation ci = callees.get(mi.ID);
            if (ci == null)
            {
                ci = new CalleeProfilingInformation(mi);
                callees.put(mi.ID, ci);
            }
            return ci;
        }
    }

    /**
     * Starts a call of this method.<br>
     * The method is pushed on the thread-stack.
     * @return true if call was not recursive.
     */
    public boolean startCall()
    {
        return ThreadProfilingInformation.getInstance().pushMethod(this);
    }

    /**
     * Ends a call of this method.<br>
     * The method is popped from thread stack and the used time is added the
     * method- and callee-information.
     *
     * @param usedValue The used measurement value.
     * @param notRecursive True if this call was not recursive.
     */
    public void endCall(MeasurementValue usedValue, boolean notRecursive)
    {
        addCall(usedValue, notRecursive );

        if ( notRecursive )
        {
            ThreadProfilingInformation ti = ThreadProfilingInformation.getInstance();
            ti.popMethod(this);
            MethodProfilingInformation mi = ti.getCurrentMethod();
            if (mi != null)
            {
                CalleeProfilingInformation ci = mi.getCalleeInformation(this);
                ci.addCall(usedValue, notRecursive);
            }
        }
    }

    @Override
    public void clear()
    {
        super.clear();
        exceptions = 0;

        synchronized (callees)
        {
            callees.clear();
        }
    }


}
