package com.dacaspex.provider.tweakers;

import org.joda.time.DateTime;

public class HeadlineItem {
    private final String id;
    private final String type;
    private final String subType;
    private final DateTime publishedAt;
    private final String title;
    private final String url;
    private final int commentCount;

    public HeadlineItem(
        String id,
        String type,
        String subType,
        DateTime publishedAt,
        String title,
        String url,
        int commentCount
    ) {
        this.id = id;
        this.type = type;
        this.subType = subType;
        this.publishedAt = publishedAt;
        this.title = title;
        this.url = url;
        this.commentCount = commentCount;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public DateTime getPublishedAt() {
        return publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getCommentCount() {
        return commentCount;
    }
}
