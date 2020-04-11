package com.bw.jtools.profiling.callgraph;

import com.bw.jtools.profiling.measurement.MeasurementValue;

import java.util.concurrent.atomic.AtomicInteger;

public class CallBase {

    private AtomicInteger idGenerator = new AtomicInteger(1);

    /**
     * The unique id.
     */
    public final int id;


    /**
     * The absolute number of calls.
     */
    public final int calls;

    /**
     * The measurement value.
     */
    public final MeasurementValue value;

    /**
     * Create a new Call.<br>
     * @param calls Absolute number of calls.
     * @param value The measured value.
     */
    protected CallBase( int calls, MeasurementValue value ) {
        this.id = idGenerator.incrementAndGet();
        this.calls = calls;
        this.value = (value == null) ? null : value.clone();
    }

}



