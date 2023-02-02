package com.dacaspex.feed.panel;

import com.dacaspex.collector.models.OrderedList;

public class OrderedListPanel extends Panel {
    private final OrderedList list;

    public OrderedListPanel(String header, OrderedList list) {
        super(header);
        this.list = list;
    }

    public OrderedList getList() {
        return list;
    }
}
