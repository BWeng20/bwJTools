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

/**
 * Base for profiling information instances.
 */
public class ProfilingInformation
{

    /**
     * The sum of all callS.
     */
    public MeasurementValue sum = null;

   /**
    * The maximum across all calls.
    */
    public MeasurementValue maxMeasurement = new MeasurementValue( new long[] { Long.MIN_VALUE } );

    /**
    * The minimum across all calls.
    */
    public MeasurementValue minMeasurement = new MeasurementValue( new long[] { Long.MAX_VALUE} );

    /**
     * Number of profiled usages.
     */
    public int calls = 0;

    /**
     * Number of recursive usages.
     */
    public int recursiveCalls = 0;

    /**
     * Adds a call.
     * @param measurementValue Measurement value
     * @param notRecursive True if this call was not recursive.
     */
    public final void addCall( MeasurementValue measurementValue, boolean notRecursive )
    {
        // Parallel access to this method from different thread would lead - in worst case - to a small numeric error, but not to some crash.
        // So synchrsonisation can be skipped in favour of performance.
        if ( notRecursive ) {
            ++calls;
            if ( sum == null )
            {
                sum = measurementValue.clone();
            }
            else {
                sum.add(measurementValue);
            }
            if ( measurementValue.greaterThan(maxMeasurement) ) maxMeasurement = measurementValue.clone();
            if ( measurementValue.lessThan(minMeasurement) ) minMeasurement = measurementValue.clone();
        } else {
            ++recursiveCalls;
        }
    }

    /**
     * Clear profiling information.
     */
    public void clear()
    {
        sum = null;
        maxMeasurement = new MeasurementValue( new long[] { Long.MIN_VALUE } );
        minMeasurement = new MeasurementValue( new long[] { Long.MAX_VALUE} );
        calls = 0;
        recursiveCalls = 0;
    }

}
