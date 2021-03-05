package com.dacaspex.feed;

import com.dacaspex.feed.panel.PanelDescriptor;
import com.dacaspex.publisher.Publisher;

import java.util.List;

public class FeedDescriptor {
    private final String name;
    private final List<Publisher> publishers;
    private final List<PanelDescriptor> panelDescriptors;

    public FeedDescriptor(String name, List<Publisher> publishers, List<PanelDescriptor> panelDescriptors) {
        this.name = name;
        this.publishers = publishers;
        this.panelDescriptors = panelDescriptors;
    }

    public String getName() {
        return name;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }

    public List<PanelDescriptor> getPanelDescriptors() {
        return panelDescriptors;
    }
}
