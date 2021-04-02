package com.dacaspex.feed.panel;

import org.joda.time.Interval;

import java.util.List;

public class PanelDescriptor {
    private final String name;
    private final String header;
    private final PanelType type;
    private final List<String> sources;
    private final Interval relevanceInterval;

    public PanelDescriptor(String name, String header, PanelType type, List<String> sources, Interval relevanceInterval) {
        this.name = name;
        this.header = header;
        this.type = type;
        this.sources = sources;
        this.relevanceInterval = relevanceInterval;
    }

    public String getName() {
        return name;
    }

    public String getHeader() {
        return header;
    }

    public PanelType getType() {
        return type;
    }

    public List<String> getSources() {
        return sources;
    }

    public Interval getRelevanceInterval() {
        return relevanceInterval;
    }
}
