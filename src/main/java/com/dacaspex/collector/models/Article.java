package com.dacaspex.collector.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Article extends AbstractItem {
    private final String title;
    private final String body;
    private final String link;
    private final DateTime publishedAt;

    public Article(String sourceId, String title, String body, String link, DateTime publishedAt) {
        super(sourceId);
        this.title = title;
        this.body = body;
        this.link = link;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getLink() {
        return link;
    }

    public DateTime getPublishedAt() {
        return publishedAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

        return String.format(
            "Article(title = %s, body = %s, link = %s, publishedAt = %s)",
            title,
            body,
            link,
            dtf.print(publishedAt)
        );
    }
}
