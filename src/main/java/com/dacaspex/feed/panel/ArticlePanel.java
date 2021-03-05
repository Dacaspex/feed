package com.dacaspex.feed.panel;

import com.dacaspex.storage.article.Article;

import java.util.List;

public class ArticlePanel extends Panel {
    private final List<Article> articles;

    public ArticlePanel(String name, String header, List<Article> articles) {
        super(name, header);
        this.articles = articles;
    }

    public String getName() {
        return name;
    }

    public String getHeader() {
        return header;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
