package com.dacaspex.storage.article;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Article {
    private int id;
    private String uuid;
    private String source;
    private String title;
    private String body;
    private String link;
    private DateTime publishedAt;

    public Article(int id, String uuid, String source, String title, String body, String link, DateTime publishedAt) {
        this.id = id;
        this.uuid = uuid;
        this.source = source;
        this.title = title;
        this.body = body;
        this.link = link;
        this.publishedAt = publishedAt;
    }

    public int getId() {
        return id;
    }

    public String getSource() {
        return source;
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
                "Article(id = %s, uuid = %s, source = %s, title = %s, body = %s, link = %s, publishedAt = %s",
                id,
                uuid,
                source,
                title,
                body,
                link,
                dtf.print(publishedAt)
        );
    }
}
