package com.dacaspex.provider.condition.string;

public class StringContainsCondition extends AdvancedStringCondition {
    public StringContainsCondition(String match) {
        super(match);
    }

    @Override
    public boolean holds(String value) {
        return match.toLowerCase().contains(value.toLowerCase());
    }
}
