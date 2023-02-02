package com.dacaspex.feed;

import com.dacaspex.collector.ItemCollector;
import com.dacaspex.feed.panel.ArticlePanel;
import com.dacaspex.feed.panel.CalendarEventsPanel;
import com.dacaspex.feed.panel.OrderedListPanel;
import com.dacaspex.feed.panel.Panel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FeedFactory {
    private final ItemCollector itemCollector;

    public FeedFactory(ItemCollector itemCollector) {
        this.itemCollector = itemCollector;
    }

    public Feed fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        List<Panel> panels = new ArrayList<>();

        for (JsonElement panelJsonElement : json.get("panels").getAsJsonArray())
        {
            JsonObject panelJson = panelJsonElement.getAsJsonObject();
            String type = panelJson.get("type").getAsString();
            switch (type) {
                case "article":
                    panels.add(articleFromJson(panelJson));
                    break;
                case "ordered_list":
                    panels.add(orderedListPanelFromJson(panelJson));
                    break;
                case "calendar_event":
                    panels.add(calendarEventsFromJson(panelJson));
                    break;
                default:
                    throw new IllegalArgumentException("no such panel type");
            }
        }

        return new Feed(id, panels);
    }

    private ArticlePanel articleFromJson(JsonObject articleJson) {
        return new ArticlePanel(
            articleJson.get("header").getAsString(),
            itemCollector.getArticles(getSourceIdsFromJson(articleJson))
        );
    }

    private CalendarEventsPanel calendarEventsFromJson(JsonObject calendarJson) {
        return new CalendarEventsPanel(
            calendarJson.get("header").getAsString(),
            itemCollector.getCalendarEvents(getSourceIdsFromJson(calendarJson))
        );
    }

    private OrderedListPanel orderedListPanelFromJson(JsonObject orderedListJson) {
        return new OrderedListPanel(
            orderedListJson.get("header").getAsString(),
            itemCollector.getOrderedList(getSourceIdsFromJson(orderedListJson))
        );
    }

    private List<String> getSourceIdsFromJson(JsonObject panelJson) {
        List<String> sourceIds = new ArrayList<>();
        panelJson.get("sourceIds")
            .getAsJsonArray()
            .forEach(element -> sourceIds.add(element.getAsString()));
        return sourceIds;
    }
}
