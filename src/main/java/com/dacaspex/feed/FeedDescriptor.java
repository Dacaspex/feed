package com.dacaspex.feed;

import com.dacaspex.feed.panel.PanelDescriptor;
import com.dacaspex.publisher.Publisher;

import java.util.List;

public class FeedDescriptor {
    private String name;
    private List<Publisher> publishers;
    private List<PanelDescriptor> panelDescriptors;

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
