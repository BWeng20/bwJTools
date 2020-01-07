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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Holds Thread Information during run-time.
 */
public final class ThreadProfilingInformation
{

    private static final class ThreadLocalProfilingInformation extends ThreadLocal<ThreadProfilingInformation>
    {

        @Override
        protected ThreadProfilingInformation initialValue()
        {
            return new ThreadProfilingInformation();
        }
    }

    private final Map<String, ClassProfilingInformation> threadClassInfo = new HashMap<>(50);
    private final static ThreadLocalProfilingInformation INSTANCE = new ThreadLocalProfilingInformation();

    private ThreadProfilingInformation()
    {

    }

    /**
     * Gets the instance for the current thread.
     *
     * @return The instance.
     */
    public static ThreadProfilingInformation getInstance()
    {
        return INSTANCE.get();
    }

    /**
     * Gets the class information for a class.
     * @param clazz The class.
     * @return The class information, never null.
     */
    public ClassProfilingInformation getClassInformation(final Class<?> clazz)
    {
        return getClassInformation(ClassProfilingInformation.SIMPLE_NAMES ? clazz.getSimpleName() : clazz.getName());
    }

    /**
     * Gets the class information for a class.<br>
     * Remind that {@link ClassProfilingInformation#SIMPLE_NAMES} is true
     * the class name should be the simple name without package-prefix.
     *
     * @param clazz The name of the class.
     * @return The class information, never null.
     */
    public ClassProfilingInformation getClassInformation(final String clazz)
    {
        ClassProfilingInformation ci = threadClassInfo.get(clazz);
        if (ci == null)
        {
            ci = ClassProfilingInformation.getClassInformation(clazz);
            threadClassInfo.put(clazz, ci);
        }
        return ci;
    }

    private List<MethodProfilingInformation> stack = new ArrayList<>(50);
    private HashSet<Integer> stackIds = new HashSet<>(50);

    /**
     * Returns the current profiled method on stack.
     * @return The method, possibly null.
     */
    public MethodProfilingInformation getCurrentMethod()
    {
        return stack.isEmpty() ? null : stack.get(stack.size() - 1);
    }

    /**
     * Checks if a method is in current stack.
     * @return True if on stack.
     */
    public boolean isOnStack(MethodProfilingInformation mi)
    {
        return stackIds.contains(mi.ID);
    }

    /**
     * Pops the method from stack.<br>
     * For robustness, all other methods until the specified one are also popped.
     * @param mi The method.
     */
    public void popMethod(MethodProfilingInformation mi)
    {
        int s = stack.size();
        if (mi != null)
        {
            while (s > 0)
            {
                final MethodProfilingInformation smi = stack.remove(--s);
                stackIds.remove(smi.ID);
                if (mi == smi)
                {
                    break;
                }
            }
        } else if (s > 0)
        {
            final MethodProfilingInformation smi = stack.remove(--s);
            stackIds.remove(smi.ID);
        }
    }

    /**
     * Push a method on stack if not already in.<br>
     * As recursion is not really possible to track with the current profiling
     * concept, recursive calls are ignored.
     *
     * @param mi The Method.
     * @return true if method was not on stack.
     */
    public boolean pushMethod(MethodProfilingInformation mi)
    {
        if (stackIds.add(mi.ID))
        {
            stack.add(mi);
            return true;
        } else
        {
            return false;
        }
    }
}
