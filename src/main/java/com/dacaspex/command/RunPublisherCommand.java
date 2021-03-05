package com.dacaspex.command;

import com.dacaspex.feed.Feed;
import com.dacaspex.feed.FeedDescriptor;
import com.dacaspex.feed.panel.*;
import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.storage.article.Article;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.storage.event.CalendarEvent;
import com.dacaspex.storage.event.EventStorage;
import com.dacaspex.storage.list.ListItem;
import com.dacaspex.storage.list.TemporaryRankedListStorage;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class RunPublisherCommand {
    private final ArticleStorage articleStorage;
    private final EventStorage eventStorage;
    private final TemporaryRankedListStorage rankedListStorage;
    private final List<FeedDescriptor> feedDescriptors;
    private final List<Provider> providers;

    public RunPublisherCommand(
        ArticleStorage articleStorage,
        EventStorage eventStorage,
        TemporaryRankedListStorage rankedListStorage,
        List<FeedDescriptor> feedDescriptors,
        List<Provider> providers
    ) {
        this.articleStorage = articleStorage;
        this.eventStorage = eventStorage;
        this.rankedListStorage = rankedListStorage;
        this.feedDescriptors = feedDescriptors;
        this.providers = providers;
    }

    public void run() {
        runAdHocProviders();
        feedDescriptors.forEach(this::handleFeed);
    }

    private void runAdHocProviders() {
        for (Provider provider : providers) {
            if (provider.getRunnableType() == RunnableType.AD_HOC) {
                provider.execute();
            }
        }
    }

    private void handleFeed(FeedDescriptor feedDescriptor) {
        List<Panel> panels = new ArrayList<>();

        // TODO: Extract this to the config
        // This program is run daily, so therefore we are only interested in articles that
        // are at most a day old
        DateTime now = new DateTime();
        DateTime yesterday = now.minusDays(1);
        DateTime sevenDaysLater = now.plusDays(7);

        // For each panel descriptor, we get the data for that panel from its appropriate place and
        // add it to the list of panels
        Panel panel;
        for (PanelDescriptor panelDescriptor : feedDescriptor.getPanelDescriptors()) {
            switch (panelDescriptor.getType()) {
                case ARTICLE:
                    List<Article> articles = articleStorage.getArticles(panelDescriptor.getSources(), yesterday);

                    panel = new ArticlePanel(
                        panelDescriptor.getName(),
                        panelDescriptor.getHeader(),
                        articles
                    );
                    panels.add(panel);
                    break;
                case CALENDAR_EVENT:
                    List<CalendarEvent> events = eventStorage.getCalendarEventsBetween(
                        panelDescriptor.getSources(),
                        now,
                        sevenDaysLater
                    );

                    panel = new CalendarEventsPanel(panelDescriptor.getName(), panelDescriptor.getHeader(), events);
                    panels.add(panel);
                    break;
                case RANKED_LIST:
                    List<ListItem> list = rankedListStorage.getList(panelDescriptor.getSources());

                    panel = new RankedListPanel(panelDescriptor.getName(), panelDescriptor.getHeader(), list);
                    panels.add(panel);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + panelDescriptor.getType());
            }
        }

        // Build the feed
        Feed feed = new Feed(feedDescriptor.getName(), panels);

        // Publish te feed using the registered publishers
        feedDescriptor.getPublishers().forEach(publisher -> publisher.publish(feed));
    }
}
