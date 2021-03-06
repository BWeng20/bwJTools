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

import com.bw.jtools.log.OS;

import java.text.NumberFormat;

/**
 * Producer interface for Measurement values.
 */
public abstract class AbstractMeasurementSource
{
    /**
     * The currently used measurement source.
     */
    public static AbstractMeasurementSource currentSource;

    static {
        if ( OS.isThreadTimeMeasurementUsed() ) {
            currentSource = new ThreadExecutionTime();
        } else {
            currentSource = new SystemNanoTime();
        }
    }

    /**
     * Convenience method as replacement for currentSource.getMeasurement.
     * @return The current value.
     */
    public static MeasurementValue measure()
    {
        return currentSource.getMeasurement();
    }

    /**
     * Convenience method as replacement for currentSource.formatValue.
     * @param nf The number format to use.
     * @param value The value to format.
     * @return The formatted value.
     */
    public static String format(NumberFormat  nf, MeasurementValue value)
    {
        return currentSource.formatValue(nf,value);
    }

    /**
     * Returns a instance that reflects the current value.
     * The first dimension have to be strictly monotone.
     * @return The current value.
     */
    public abstract MeasurementValue getMeasurement();

    /**
     * Returns the formatted value.
     * @param nf The number format to use.
     * @param value The value to format.
     * @return The formatted value.
     */
    public abstract String formatValue( NumberFormat  nf, MeasurementValue value );

}
