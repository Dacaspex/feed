package com.dacaspex.provider.rss;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.storage.article.ArticleStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RssProvider implements Provider {
    private final static Logger logger = LogManager.getLogger();

    private final RssReader rssReader;
    private final String name;
    private final String url;
    private final String source;
    private final ArticleStorage articleStorage;

    public RssProvider(String name, String url, String source, ArticleStorage articleStorage) {
        this.rssReader = new RssReader();
        this.name = name;
        this.url = url;
        this.source = source;
        this.articleStorage = articleStorage;
    }

    @Override
    public RunnableType getRunnableType() {
        return RunnableType.ANYTIME;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        // Read from the RSS feed
        Stream<Item> feed;
        try {
            feed = rssReader.read(url);
        } catch (IOException e) {
            logger.error(e);
            return;
        }

        // Store articles
        List<Item> articles = feed.collect(Collectors.toList());
        for (Item article : articles) {
            DateTime date = extractDateTime(article);
            if (date == null) {
                continue;
            }

            articleStorage.insertOrUpdateArticle(
                article.getGuid().orElseThrow(),
                source,
                article.getTitle().orElseThrow(),
                article.getDescription().orElseThrow(),
                article.getLink().orElseThrow(),
                date
            );
        }
    }

    private DateTime extractDateTime(Item article) {
        // Attempt to parse the publish date, with time zone data. However, this crashes for some RSS feeds
        // which provide the data in a different format
        try {
            ZonedDateTime zdt = article.getPubDateZonedDateTime().orElseThrow();
            DateTimeZone zone = DateTimeZone.forID(zdt.getZone().getId());
            return new DateTime(zdt.toInstant().toEpochMilli(), zone);
        } catch (IllegalArgumentException e) {
            // Intentionally ignored. The data is provided in another format
        }

        // Here we try to parse the other format(s)
        try {
            return DateTimeFormat
                .forPattern("EEE, dd MMM yyyy HH:mm:ss Z")
                .withLocale(Locale.US)
                .parseDateTime(article.getPubDate().orElseThrow());
        } catch (Exception e) {
            // Intentionally left blank
        }

        try {
            return DateTimeFormat
                .forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .parseDateTime(article.getPubDate().orElseThrow());
        } catch (Exception e) {
            // Intentionally left blank
        }

        logger.error(String.format("Could not parse date (%s) from RSS (%s) article", article.getPubDate(), name));
        return null;
    }
}
