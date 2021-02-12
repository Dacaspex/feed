package com.dacaspex.feed.panel;

public class Panel {
    protected final String name;
    protected final String header;

    public Panel(String name, String header) {
        this.name = name;
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public String getHeader() {
        return header;
    }
}
