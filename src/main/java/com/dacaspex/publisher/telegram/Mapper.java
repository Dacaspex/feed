package com.dacaspex.publisher.telegram;

import com.dacaspex.feed.panel.ArticlePanel;
import com.dacaspex.feed.panel.CalendarEventsPanel;
import com.dacaspex.feed.panel.RankedListPanel;
import com.dacaspex.storage.article.Article;
import com.dacaspex.storage.event.CalendarEvent;
import com.dacaspex.storage.list.ListItem;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Mapper {
    public String mapRankedListToString(RankedListPanel rankedListPanel) {
        String header = String.format("<b>%s</b>", rankedListPanel.getHeader());
        StringBuilder events = new StringBuilder();

        for (ListItem item : rankedListPanel.getList()) {
            events.append(mapRankedListItemToString(item));
        }

        return String.format("%s\n%s", header, events.toString());
    }

    public String mapCalendarEventsPanelToString(CalendarEventsPanel panel) {
        String header = String.format("<b>%s</b>", panel.getHeader());
        StringBuilder events = new StringBuilder();

        for (CalendarEvent event : panel.getEvents()) {
            events.append(mapCalendarEventToString(event));
        }

        return String.format("%s\n%s", header, events.toString());
    }

    public String mapArticlePanelToString(ArticlePanel panel) {
        // TODO: Create different display when there are no articles

        // We can use some HTML tags in the string, see https://core.telegram.org/bots/api#html-style
        String header = String.format("<b>%s</b>", panel.getHeader());
        StringBuilder articles = new StringBuilder();

        for (Article article : panel.getArticles()) {
            articles.append(mapArticleToString(article));
        }

        return String.format("%s\n%s", header, articles.toString());
    }

    private String mapRankedListItemToString(ListItem item) {
        return String.format(
            "•  %s [<a href=\"%s\">link</a>]\n",
            item.getContent(),
            item.getUrl()
        );
    }

    private String mapCalendarEventToString(CalendarEvent event) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMM");

        return String.format(
            "•  %s - %s [<a href=\"%s\">link</a>]\n",
            dtf.print(event.getDate()),
            event.getHeader(),
            event.getUrl()
        );
    }

    private String mapArticleToString(Article article) {
        // We split up the date and time to make sure the divider "|" is not rendered in italics
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd-MM");
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

        // We can use some HTML tags in the string, see https://core.telegram.org/bots/api#html-style
        return String.format(
            "•  <i>%s</i> | <i>%s</i> <a href=\"%s\">%s</a> \n",
            dateFormat.print(article.getPublishedAt()),
            timeFormat.print(article.getPublishedAt()),
            article.getLink(),
            article.getTitle()
        );
    }
}
