package com.dacaspex.provider;

import com.dacaspex.collector.ItemCollector;

public abstract class AbstractProvider implements Provider {
    protected final String id;
    protected final ItemCollector itemCollector;

    public AbstractProvider(String id, ItemCollector itemCollector) {
        this.id = id;
        this.itemCollector = itemCollector;
    }

    @Override
    public String getId() {
        return id;
    }
}
