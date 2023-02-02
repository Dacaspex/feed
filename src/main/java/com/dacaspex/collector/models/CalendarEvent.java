package com.dacaspex.collector.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CalendarEvent extends AbstractItem {
    private final DateTime date;
    private final String header;
    private final String url;

    public CalendarEvent(String sourceId, DateTime date, String header, String url) {
        super(sourceId);
        this.date = date;
        this.header = header;
        this.url = url;
    }

    public DateTime getDate() {
        return date;
    }

    public String getHeader() {
        return header;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

        return String.format(
            "CalendarEvent(header = %s, date = %s, url = %s)",
            header,
            dtf.print(date),
            date
        );
    }
}
