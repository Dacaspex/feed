package com.dacaspex.collector.models;

public abstract class AbstractItem {
    protected final String sourceId;

    public AbstractItem(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceId() {
        return sourceId;
    }
}
