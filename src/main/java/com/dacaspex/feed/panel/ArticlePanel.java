package com.dacaspex.feed.panel;

import com.dacaspex.collector.models.Article;

import java.util.List;

public class ArticlePanel extends Panel {
    private final List<Article> articles;

    public ArticlePanel(String header, List<Article> articles) {
        super(header);
        this.articles = articles;
    }

    public String getHeader() {
        return header;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
