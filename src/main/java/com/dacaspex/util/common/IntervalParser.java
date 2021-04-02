package com.dacaspex.util.common;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class IntervalParser {
    public Interval centeredDayInterval(DateTime center, int daysBefore, int daysAfter) {
        return new Interval(center.plusDays(daysBefore), center.plusDays(daysAfter));
    }
}
