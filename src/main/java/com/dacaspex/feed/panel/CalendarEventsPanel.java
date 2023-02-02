package com.dacaspex.feed.panel;

import com.dacaspex.collector.models.CalendarEvent;

import java.util.List;

public class CalendarEventsPanel extends Panel {
    private final List<CalendarEvent> events;

    public CalendarEventsPanel(String header, List<CalendarEvent> events) {
        super(header);
        this.events = events;
    }

    public List<CalendarEvent> getEvents() {
        return events;
    }
}
