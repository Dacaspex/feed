package com.dacaspex.provider.nos;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.dacaspex.provider.Provider;
import com.dacaspex.storage.article.ArticleStorage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NosProvider implements Provider {
    private RssReader rssReader;
    private String url;
    private ArticleStorage articleStorage;

    public NosProvider(String url, ArticleStorage articleStorage) {
        this.rssReader = new RssReader();
        this.url = url;
        this.articleStorage = articleStorage;
    }

    @Override
    public void invoke() {
        // Read from the RSS feed
        Stream<Item> feed;
        try {
            feed = rssReader.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // TODO: Add NLP to determine subject of article to generate tags

        List<Item> articles = feed.collect(Collectors.toList());
        for (Item article : articles) {
            // Create a compatible date time object from the given date time in the Item class
            ZonedDateTime zdt = article.getPubDateZonedDateTime().orElseThrow();
            DateTimeZone zone = DateTimeZone.forID(zdt.getZone().getId());
            DateTime dt = new DateTime(zdt.toInstant().toEpochMilli(), zone);

            // Store article
            articleStorage.insertOrUpdateArticle(
                    article.getGuid().orElseThrow(),
                    "nos",
                    article.getTitle().orElseThrow(),
                    removeHtml(article.getDescription().orElseThrow()),
                    article.getLink().orElseThrow(),
                    dt
            );
        }
    }

    /**
     * The RSS feed of NOS contains paragraph and heading tags. For now we just remove those since
     * that is presumably easier to handle.
     */
    private String removeHtml(String string) {
        return string
                .replaceAll("<p>", "")
                .replaceAll("</p>", "")
                .replaceAll("<h2>", "")
                .replaceAll("</h2>", "");
    }
}
