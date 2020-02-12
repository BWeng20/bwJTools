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

import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * J2EE Interceptor for profiling.
 */
public class ProfilingInterceptor
{

    /**
     * Profiles an annotated EJB-call.
     * @param ctx The call context
     * @return The return value.
     * @throws Exception any throw exception.
     */
    @AroundInvoke
    public Object profile(InvocationContext ctx) throws Exception {

        final Method m = ctx.getMethod();
        final ClassProfilingInformation ci = ThreadProfilingInformation.getInstance().getClassInformation(m.getDeclaringClass());
        final MethodProfilingInformation mi = ci.getMethodInformation(m.getName());

        MethodProfiling mp = new MethodProfiling(mi);
        try {
            return ctx.proceed();
        } catch (Exception e) {
            mp.exception(e);
            throw e;
        }
        finally
        {
            mp.close();
        }
    }

}
