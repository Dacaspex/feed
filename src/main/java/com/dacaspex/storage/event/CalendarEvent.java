package com.dacaspex.storage.event;

import org.joda.time.DateTime;

public class CalendarEvent {
    private final String id;
    private final String uuid;
    private final DateTime date;
    private final String header;
    private final String url;

    public CalendarEvent(String id, String uuid, DateTime date, String header, String url) {
        this.id = id;
        this.uuid = uuid;
        this.date = date;
        this.header = header;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
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
}
