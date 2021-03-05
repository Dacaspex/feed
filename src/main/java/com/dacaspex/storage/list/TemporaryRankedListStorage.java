package com.dacaspex.storage.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Currently, this implementation is used everywhere because there is no need for
 * persistent storage for ranked lists.
 */
public class TemporaryRankedListStorage {
    private final Map<String, List<ListItem>> storage;

    public TemporaryRankedListStorage() {
        this.storage = new HashMap<>();
    }

    public void addList(String source, List<ListItem> list) {
        storage.put(source, list);
    }

    /**
     * Get the ranked list of the sources that are specified. Note that when multiple sources
     * are given, the lists coming from the separate sources are simply concatenated. This is
     * sufficient for the current use case.
     */
    public List<ListItem> getList(List<String> sources) {
        List<ListItem> accumulator = new ArrayList<>();
        for (String source : sources) {
            accumulator.addAll(storage.getOrDefault(source, new ArrayList<>()));
        }
        return accumulator;
    }
}
