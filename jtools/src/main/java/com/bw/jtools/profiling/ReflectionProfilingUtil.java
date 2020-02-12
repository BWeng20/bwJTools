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
import java.util.stream.Stream;

/**
 * Static methods for reflection handling inside profiling.
 */
public final class ReflectionProfilingUtil
{
    public static enum StackAccessMode {

        /** StackWalker, since Java 9. */
        STACKWALKER,

        /** Via protected Throwable-Method, works through Java 8. */
        GETSTACKTRACEELEMENT,

        /** Fail-safe via {@link java.lang.Throwable#getStackTrace()}, works always but is slower by factor 10. */
        FAILSAFE,

        /** Try to detect the best way. */
        AUTO;
    }

    /**
     * Will be set to true during initialization if Java9 "StackWalker" should
     * be used.<br>
     * See static initialization.
     */
    private static StackAccessMode stackAccessMode = StackAccessMode.AUTO;

    /**
     * For prior Java9 a protected method of "Throwable" will be sued to access
     * a specific stack-trace-element.
     */
    private static Method throwableGetStackTraceElement;

    static
    {
        setStackTraceAccessMode( StackAccessMode.AUTO );
    }

    /**
     * Sets a new stack-trace-access mode, used to retrieve caller-methods.
     * @param mode The mode to set.
     */
    public static void setStackTraceAccessMode( StackAccessMode mode )
    {
        if ( mode == StackAccessMode.AUTO )
        {
            try
            {
                Class.forName("java.lang.StackWalker");
                stackAccessMode = StackAccessMode.STACKWALKER;
            }
            catch (Exception t)
            {
                try
                {
                    throwableGetStackTraceElement = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
                    throwableGetStackTraceElement.setAccessible(true);
                    stackAccessMode = StackAccessMode.GETSTACKTRACEELEMENT;
                } catch (Exception e)
                {
                    // If exception is NoSuchMethodException, we should have Jdk 9+, but 9 has StackWalker.
                    stackAccessMode = StackAccessMode.FAILSAFE;
                }
            }
        } else
        {
            stackAccessMode = mode;
        }
    }

    /**
     * The current stack-trace-access mode.
     * @return The current mode.
     */
    public static StackAccessMode getStackTraceAccessMode()
    {
        return stackAccessMode;
    }

    /**
     * Index of the calling method of a method that calls
     * getStackTraceElement.<br>
     * <i>This value may diff for other JVMs and possibly needs adaption during
     * run-time.</i>
     */
    public static int CALLING_METHOD_STACK_INDEX = 2;

    /**
     * Gets a stack-trace-element with minimal overhead.
     *
     * @param level 1 for caller of this method and +1 for each caller above.
     * @return The Trace Element.
     */
    public static StackTraceElement getStackTraceElement(int level)
    {
        if ( stackAccessMode == StackAccessMode.STACKWALKER )
        {
            try
            {
                StackWalker.StackFrame result = StackWalker.getInstance().walk((Stream<StackWalker.StackFrame> stream) ->
                {
                    return stream.skip(level).limit(1).findFirst().orElse(null);
                });
                return result != null ? result.toStackTraceElement() : null;
            }
            catch (ArrayIndexOutOfBoundsException ai)
            {
                return null;
            }
            catch (Throwable t)
            {
                // Failed to access stack-trace, switching to fail-safe mode
                stackAccessMode = StackAccessMode.FAILSAFE;
            }
        }
        else if ( stackAccessMode == StackAccessMode.GETSTACKTRACEELEMENT )
        {
            try
            {
                return (StackTraceElement)throwableGetStackTraceElement.invoke(new Throwable(), level);
            }
            catch (Throwable t)
            {
                // Failed to access stack-trace, switching to fail-safe mode
                stackAccessMode = StackAccessMode.FAILSAFE;
            }
        }

        // Use the really slow (10x) but fail-safe method.
        // Remind, that some VMs (and really old versions) may NOT return the elements as expected!
        return new Throwable().getStackTrace()[level];
    }

    /**
     * Normalize a class name according to settings.
     * @param className The name of the class.
     * @return The normalized class name.
     */
    public static String normalizeClassName(final String className)
    {
        if (className != null && ClassProfilingInformation.SIMPLE_NAMES)
        {
            final int lastDot = className.lastIndexOf('.');
            if (lastDot >= 0)
            {
                return className.substring(lastDot + 1);
            } else
            {
                return className;
            }
        } else
        {
            return className;
        }
    }
}
