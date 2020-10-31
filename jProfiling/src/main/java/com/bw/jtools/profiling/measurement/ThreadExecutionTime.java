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
 * Measures monotonic and thread-local time in nanoseconds.
 */
public final class ThreadExecutionTime extends AbstractMeasurementSource
{

    @Override
    public MeasurementValue getMeasurement()
    {
        return new MeasurementValue(new long[]
        {
            System.nanoTime(), OS.getThreadExecutionTimeNS()
        });
    }

    @Override
    public String formatValue(NumberFormat nf, MeasurementValue value)
    {
        StringBuilder sb = new StringBuilder(30);
        sb
                .append(nf.format(value.values[0] / 1000000000.0)).append("s/")
                .append(nf.format(value.values[1] / 1000000000.0)).append('s');
        return sb.toString();
    }
}
