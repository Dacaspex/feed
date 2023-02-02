package com.dacaspex.provider.rss;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.dacaspex.collector.ItemCollector;
import com.dacaspex.collector.models.Article;
import com.dacaspex.provider.AbstractProvider;
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

public class RssProvider extends AbstractProvider {
    private final static Logger logger = LogManager.getLogger();

    private final RssProviderSettings settings;
    private final RssReader rssReader;

    public RssProvider(String id, ItemCollector itemCollector, RssProviderSettings settings) {
        super(id, itemCollector);
        this.settings = settings;
        this.rssReader = new RssReader();
    }

    @Override
    public void execute() {
        // Read from the RSS feed
        Stream<Item> feed;
        try {
            feed = rssReader.read(settings.url);
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

            itemCollector.addArticle(
                new Article(
                    id,
                    article.getTitle().orElseThrow(),
                    article.getDescription().orElseThrow(),
                    article.getLink().orElseThrow(),
                    date
                )
            );
        }
    }

    private DateTime extractDateTime(Item article) {
        // Attempt to parse the published date, with time zone data. However, this crashes for some RSS feeds
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

        logger.error(String.format("Could not parse date (%s) from RSS (%s) article", article.getPubDate(), id));
        return null;
    }
}
