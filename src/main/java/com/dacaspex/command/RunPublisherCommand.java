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

        // For each panel descriptor, we get the data for that panel from its appropriate place and
        // add it to the list of panels
        Panel panel;
        for (PanelDescriptor panelDescriptor : feedDescriptor.getPanelDescriptors()) {
            switch (panelDescriptor.getType()) {
                case ARTICLE:
                    // TODO: Change fetching of articles to a proper date time interval, instead of a half
                    //   open interval as is right now
                    DateTime after = panelDescriptor.getRelevanceInterval().getStart();
                    List<Article> articles = articleStorage.getArticles(panelDescriptor.getSources(), after);

                    if (articles.size() == 0) {
                        break;
                    }

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
                        panelDescriptor.getRelevanceInterval().getStart(),
                        panelDescriptor.getRelevanceInterval().getEnd()
                    );

                    if (events.size() == 0) {
                        break;
                    }

                    panel = new CalendarEventsPanel(panelDescriptor.getName(), panelDescriptor.getHeader(), events);
                    panels.add(panel);
                    break;
                case RANKED_LIST:
                    // TODO: Implement relevance interval for the ranked list as well. Right now it is just an ad hoc
                    //   provider but we can do something with that
                    List<ListItem> list = rankedListStorage.getList(panelDescriptor.getSources());

                    if (list.size() == 0) {
                        break;
                    }

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
