package com.dacaspex.feed.panel;

import java.util.List;

public class PanelDescriptor {
    private final String name;
    private final String header;
    private final PanelType type;
    private final List<String> sources;

    public PanelDescriptor(String name, String header, PanelType type, List<String> sources) {
        this.name = name;
        this.header = header;
        this.type = type;
        this.sources = sources;
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
}
