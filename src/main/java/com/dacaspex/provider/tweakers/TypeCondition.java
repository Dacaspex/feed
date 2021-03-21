package com.dacaspex.provider.tweakers;

import com.dacaspex.provider.condition.Condition;

public class TypeCondition implements Condition<HeadlineItem> {
    private final String type;

    public TypeCondition(String type) {
        this.type = type;
    }

    public boolean holds(HeadlineItem item) {
        return item.getType().equalsIgnoreCase(type);
    }
}
