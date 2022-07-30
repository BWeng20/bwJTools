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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds profiling information about a class during run-time.
 */
public final class ClassProfilingInformation extends IdentifiableProfilingInformation
{

    /**
     * The name of the class.
     */
    public final String name;

    /**
     * Global flag to control class name usage.<br>
     * If "true", simple class names are used. See {@link java.lang.Class#getSimpleName() }.
     */
    public static boolean SIMPLE_NAMES = true;

    private final static Map<String,ClassProfilingInformation> classInfo = new HashMap<>(50);

    private final Map<String,MethodProfilingInformation> methods = new HashMap<>(15);

    private static Calendar startOfProfiling = Calendar.getInstance();

    /**
     * Creates a new Class Information object.
     * @param name The name.
     */
    public ClassProfilingInformation(final String name)
    {
        this.name = name;
    }

    /**
     * Get all class profiling information.
     * @return The list of class information.
     */
    public static List<ClassProfilingInformation> getClassInformation( )
    {
        synchronized( classInfo ) {
            List<ClassProfilingInformation> l = new ArrayList<>(classInfo.size());
            l.addAll(classInfo.values());
            return l;
        }
    }

    /**
     * Clear all profiling information from class/method information.
     */
    public static void clearProfilingInformation( )
    {
        synchronized( classInfo ) {
            startOfProfiling = Calendar.getInstance();
            for ( ClassProfilingInformation ci : classInfo.values()) {
                ci.clear();
            }
        }
    }

    /**
     * Get start date and time.
     * @return The current system time of start of profiling.
     */
    public static Calendar getProfilingStartTime( )
    {
        return startOfProfiling;
    }

    /**
     * Get the class information for the module/class.<br>
     * If feasible, use {@link ThreadProfilingInformation#getClassInformation(java.lang.String) }, especially if the thread information instance is already in use.
     * Use of the thread-local ThreadProfilingInformation-instance can reduce synchronization overhead.
     * @param clazz The class. The simple name if SIMPLE_NAMES is active, otherwise the full name.
     * @return The class information instance.
     */
    public static ClassProfilingInformation getClassInformation( final String clazz )
    {
        ClassProfilingInformation ci;
        synchronized( classInfo ) {
            ci = classInfo.get(clazz);
            if ( ci == null ) {
                ci = new ClassProfilingInformation(clazz);
                classInfo.put( clazz, ci );
            }
        }
        return ci;
    }

    /**
     * Gets all methods .<br>
     * The method is thread-safe.
     * @return The Methods
     */
    public List<MethodProfilingInformation> getMethodInformation()
    {
        synchronized( methods )
        {
            ArrayList<MethodProfilingInformation> l = new ArrayList<>(methods.size());
            l.addAll( methods.values() );
            return l;
        }
    }

    /**
     * Gets or creates the information object for the method.<br>
     * The method is thread-safe.
     * @param methodName The name of the method.
     * @return The Method-Information instance
     */
    public MethodProfilingInformation getMethodInformation(String methodName)
    {
        synchronized( methods )
        {
            MethodProfilingInformation mi = methods.get(methodName);
            if ( mi == null )
            {
                mi = new MethodProfilingInformation(this, methodName);
                methods.put( methodName, mi);
            }
            return mi;
        }
    }

    @Override
    public void clear()
    {
        synchronized( methods )
        {
            for ( MethodProfilingInformation mi : methods.values() )
            {
                mi.clear();
            }
        }

    }

}
