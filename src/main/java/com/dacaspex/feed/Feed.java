package com.dacaspex.feed;

import com.dacaspex.feed.panel.Panel;

import java.util.List;

public class Feed {
    public String name;
    public List<Panel> panels;

    public Feed(String name, List<Panel> panels) {
        this.name = name;
        this.panels = panels;
    }

    public String getName() {
        return name;
    }

    public List<Panel> getPanels() {
        return panels;
    }
}
