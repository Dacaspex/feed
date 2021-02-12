package com.dacaspex.feed.panel;

import com.dacaspex.storage.event.CalendarEvent;

import java.util.List;

public class CalendarEventsPanel extends Panel {
    private final List<CalendarEvent> events;

    public CalendarEventsPanel(String name, String header, List<CalendarEvent> events) {
        super(name, header);
        this.events = events;
    }

    public List<CalendarEvent> getEvents() {
        return events;
    }
}
