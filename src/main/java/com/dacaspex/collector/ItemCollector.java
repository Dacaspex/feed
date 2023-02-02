package com.dacaspex.collector;

import com.dacaspex.collector.models.AbstractItem;
import com.dacaspex.collector.models.Article;
import com.dacaspex.collector.models.CalendarEvent;
import com.dacaspex.collector.models.OrderedList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCollector {
    private final List<Article> articles;
    private final List<OrderedList> orderedLists;
    private final List<CalendarEvent> calendarEvents;

    public ItemCollector() {
        this.articles = new ArrayList<>();
        this.orderedLists = new ArrayList<>();
        this.calendarEvents = new ArrayList<>();
    }

    public void addArticle(Article article)
    {
        System.out.println(article);
        articles.add(article);
    }

    public void addOrderedList(OrderedList list) {
        list.getItems().forEach(System.out::println);
        orderedLists.add(list);
    }

    public void addCalendarEvent(CalendarEvent calendarEvent) {
        System.out.println(calendarEvent);
        calendarEvents.add(calendarEvent);
    }

    public List<Article> getArticles(List<String> sourceIds) {
        return getItems(articles, sourceIds);
    }

    public List<CalendarEvent> getCalendarEvents(List<String> sourceIds) {
        return getItems(calendarEvents, sourceIds);
    }

    public OrderedList getOrderedList(List<String> sourceIds) {
        return getItems(orderedLists, sourceIds).stream().findFirst().orElseThrow();
    }

    private <T extends AbstractItem> List<T> getItems(List<T> items, List<String> sourceIds) {
        return items.stream()
            .filter(item -> sourceIds.contains(item.getSourceId()))
            .collect(Collectors.toList());
    }
}
