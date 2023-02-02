package com.dacaspex.feed;

import com.dacaspex.feed.panel.Panel;

import java.util.List;

public class Feed {
    private final String id;
    private final List<Panel> panels;

    public Feed(String id, List<Panel> panels) {
        this.id = id;
        this.panels = panels;
    }

    public String getId() {
        return id;
    }

    public List<Panel> getPanels() {
        return panels;
    }
}
