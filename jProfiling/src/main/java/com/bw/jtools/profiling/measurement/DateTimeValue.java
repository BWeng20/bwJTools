package com.bw.jtools.profiling.measurement;

import java.util.Calendar;

/**
 * A Time measurement value.
 */
public class DateTimeValue extends MeasurementValue {

    /**
     * Create a new instance with values from the specified time.
     *
     * @param time Time to use.
     */
    public DateTimeValue(Calendar time) {
        super(new long[]
                {time.get(Calendar.YEAR), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH),
                        time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND)
                });
    }

    /**
     * Construct a DateTime from a value array.
     * @param v Have to contain 6 values: years,months,days,hours,minutes,seconds.
     */
    public DateTimeValue(long[] v) {
        super(v);
    }

    /**
     * Converts this instance to a calendar value.
     *
     * @return The value as {@link Calendar}
     */
    public Calendar toTime() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.YEAR, (int) values[0]);
        time.set(Calendar.MONTH, (int) values[1] - 1);
        time.set(Calendar.DAY_OF_MONTH, (int) values[2]);
        time.set(Calendar.HOUR_OF_DAY, (int) values[3]);
        time.set(Calendar.MINUTE, (int) values[4]);
        time.set(Calendar.SECOND, (int) values[5]);
        return time;
    }

    /**
     * Formats the value as ISO 8601 date and time.
     * @return The ISO 8601 formated date and time.
     */
    public String toISO8601() {
        StringBuilder sb = new StringBuilder(20);
        sb.append((int) values[0]).append('-').append((int) values[1]).append('-').append((int) values[2])
                .append('T').append((int) values[3]).append(':').append((int) values[4]).append(':').append((int) values[5]);
        return sb.toString();
    }

    @Override
    public MeasurementValue clone() {
        return new DateTimeValue(values.clone());
    }

}
