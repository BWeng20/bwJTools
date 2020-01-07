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

import com.bw.jtools.profiling.measurement.AbstractMeasurementSource;
import com.bw.jtools.profiling.measurement.MeasurementValue;

/**
 * Holds information of a currently profiled method/code-unit.
 */
public final class MethodProfiling implements AutoCloseable
{

    /**
     * Start time in nanoseconds.
     */
    public final MeasurementValue startTime;

    /**
     * Used times in nanoseconds. Null if the call is not yet finished.
     */
    protected MeasurementValue usedTime;

    /**
     * True if the call was not recursive.
     */
    protected final boolean notRecursive;

    /**
     * Get the used time in nanoseconds.
     * @return The used time in nanoseconds or null if the call is not yet finished.
     */
    public final MeasurementValue getUsedTime()
    {
        return usedTime;
    }

    /**
     * The method or code-unit that is profiled.
     */
    public final MethodProfilingInformation method;

    /**
     * C'tor that automatically detects calling method and class.<br>
     * Depending on platform this may an expensive operation. See {@link ReflectionProfilingUtil#getStackTraceElement(int)}.
     */
    public MethodProfiling()
    {
        startTime = AbstractMeasurementSource.currentSource.measure();
        StackTraceElement ste = ReflectionProfilingUtil.getStackTraceElement(ReflectionProfilingUtil.CALLING_METHOD_STACK_INDEX);
        method = ThreadProfilingInformation.getInstance().getClassInformation(ReflectionProfilingUtil.normalizeClassName(ste.getClassName()) ).getMethodInformation( ste.getMethodName() );
        notRecursive = this.method.startCall();
    }

    /**
     * C'tor with class and method.
     * @param clazz The name of the module/class.
     * @param method The name of the method/code-unit.
     */
    public MethodProfiling(final String clazz, final String method)
    {
        startTime = AbstractMeasurementSource.currentSource.measure();
        ThreadProfilingInformation ti = ThreadProfilingInformation.getInstance();
        this.method = ti.getClassInformation(clazz).getMethodInformation( method );
        notRecursive = this.method.startCall();
    }

    /**
     * C'tor with method information instance (the fasted way).
     * @param method The method to profile.
     */
    public MethodProfiling(MethodProfilingInformation method)
    {
        startTime = AbstractMeasurementSource.currentSource.measure();
        this.method = method;
        notRecursive = this.method.startCall();
    }

    /**
     * Profiles a exception.<br>
     * Remind that "close" needs still to be called - following the AutoClosable pattern.
     * @param t The Throwable.
     */
    public void exception(Throwable t)
    {
        method.exceptions++;
    }


    @Override
    public void close()
    {
        usedTime = AbstractMeasurementSource.currentSource.measure();
        usedTime.subtract(startTime);

        method.endCall(usedTime, notRecursive );
    }
}
