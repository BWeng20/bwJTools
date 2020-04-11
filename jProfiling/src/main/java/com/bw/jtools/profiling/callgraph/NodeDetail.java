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
package com.bw.jtools.profiling.callgraph;

import com.bw.jtools.profiling.measurement.MeasurementValue;

/**
 * Detail information.
 */
public class NodeDetail {

    /** Details Start-Time. Value: Year,Month,Day,Hour,Minute,Second */
    public final static int DETAIL_START = 1;

    /** Details End-Time. Value: Year,Month,Day,Hour,Minute,Second */
    public final static int DETAIL_END = 2;

    /** Details Minimum. Value: Measurement. */
    public final static int DETAIL_MINIMUM = 3;

    /** Details Maximum. Value: Measurement. */
    public final static int DETAIL_MAXIMUM = 4;

    /** ID of this detail */
    public final int ID;

    /** value of this detail */
    public final MeasurementValue value;

    public NodeDetail(int id, MeasurementValue value) {
        this.ID = id;
        this.value = value.clone();
    }

}
