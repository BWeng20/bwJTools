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
package com.bw.jtools.profiling.measurement;

/**
 * Holds a measurement value.<br>
 * Holds a value that is produced by some measurement function.<br>
 * The first dimension defines the order relation and have to be strictly monotone.
 */
public class MeasurementValue implements Comparable<MeasurementValue>, Cloneable
{

    public final long[] values;

    /**
     * C'tor to be used by all inheritances.
     * @param values The initial value
     */
    public MeasurementValue( final long[] values )
    {
        this.values = values;
    }

    @Override
    public MeasurementValue clone()
    {
        return new MeasurementValue( values.clone() );
    }

    /**
     * Subtract some value.
     * @param other The value to subtract.
     */
    public void subtract(MeasurementValue other)
    {
        final int n = values.length;
        for ( int i=0 ; i<n ; ++i) {
            values[i] -= other.values[i];
        }
    }

    /**
     * Adds some value.
     * @param other The value to add.
     */
    public void add(MeasurementValue other)
    {
        final int n = values.length;
        for ( int i=0 ; i<n ; ++i) {
            values[i] += other.values[i];
        }
    }

    @Override
    public int compareTo(MeasurementValue other)
    {
        return (int)(values[0] - other.values[0]);
    }

    /**
     * Convenience variant. Same as "compareTo(other) &lt; 0".
     * @param other The other instance.
     * @return true if this instance is less than the other.
     */
    public boolean lessThan(MeasurementValue other)
    {
        return (values[0] < other.values[0]);
    }

    /**
     * Convenience variant. Same as "compareTo(other) &gt; 0".
     * @param other The other instance.
     * @return true if this instance is greater than the other.
     */
    public boolean greaterThan(MeasurementValue other)
    {
        return (values[0] > other.values[0]);
    }
}
