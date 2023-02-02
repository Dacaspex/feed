package com.dacaspex.collector.models;

public class ListItem {
    private final String content;
    private final String url;

    public ListItem(String content, String url) {
        this.content = content;
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("ListItem(content = %s, url = %s)", content, url);
    }
}
