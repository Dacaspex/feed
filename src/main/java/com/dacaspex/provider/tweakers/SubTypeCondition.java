package com.dacaspex.provider.tweakers;

import com.dacaspex.provider.condition.Condition;

public class SubTypeCondition implements Condition<HeadlineItem> {
    private final String subType;

    public SubTypeCondition(String subType) {
        this.subType = subType;
    }

    public boolean holds(HeadlineItem item) {
        return item.getSubType().equalsIgnoreCase(subType);
    }
}
