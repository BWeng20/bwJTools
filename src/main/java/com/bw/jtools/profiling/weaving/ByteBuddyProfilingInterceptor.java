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
package com.bw.jtools.profiling.weaving;

import com.bw.jtools.profiling.MethodProfiling;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * Interceptor for use with Byte Buddy.
 */
public final class ByteBuddyProfilingInterceptor
{

    /**
     * Interceptor method for none static methods.
     * @param zuper    The intercepted method-call
     * @param method   The method
     * @return         The return value of the intercepted method
     * @throws Throwable Any by the intercepted method thrown ThroWable.
     */
    @RuntimeType
    static public Object profileReturn(@SuperCall Callable<?> zuper, @Origin Method method) throws Throwable
    {

        final MethodProfiling mp = new MethodProfiling(method.getDeclaringClass().getName(), method.getName());
        try
        {
            if (zuper != null)
            {
                return zuper.call();
            } else
            {
                return null;
            }
        } catch (Throwable t)
        {
            mp.exception(t);
            throw t;
        } finally
        {
            mp.close();
        }
    }
}
