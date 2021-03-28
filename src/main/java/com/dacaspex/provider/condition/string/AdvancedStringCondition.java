package com.dacaspex.provider.condition.string;

import com.dacaspex.provider.condition.Condition;

public abstract class AdvancedStringCondition implements Condition<String> {
    protected final String match;

    public AdvancedStringCondition(String match) {
        this.match = match;
    }

    @Override
    public abstract boolean holds(String value);
}
