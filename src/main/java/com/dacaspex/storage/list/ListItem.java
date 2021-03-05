package com.dacaspex.storage.list;

public class ListItem {
    private final String uuid;
    private final String content;
    private final String url;

    public ListItem(String uuid, String content, String url) {
        this.uuid = uuid;
        this.content = content;
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
