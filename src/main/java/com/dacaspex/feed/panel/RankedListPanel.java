package com.dacaspex.feed.panel;

import com.dacaspex.storage.list.ListItem;

import java.util.List;

public class RankedListPanel extends Panel {
    private final List<ListItem> list;

    public RankedListPanel(String name, String header, List<ListItem> list) {
        super(name, header);
        this.list = list;
    }

    public List<ListItem> getList() {
        return list;
    }
}
