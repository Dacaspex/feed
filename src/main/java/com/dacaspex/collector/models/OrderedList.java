package com.dacaspex.collector.models;

import java.util.List;

public class OrderedList extends AbstractItem {
    private final List<ListItem> items;

    public OrderedList(String sourceId, List<ListItem> items) {
        super(sourceId);
        this.items = items;
    }

    public List<ListItem> getItems() {
        return items;
    }
}
