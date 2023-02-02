package com.dacaspex.publisher;

import com.dacaspex.feed.Feed;

public abstract class AbstractPublisher {
    private final String feedId;

    public AbstractPublisher(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedId() {
        return feedId;
    }

    public abstract void publish(Feed feed);
}
