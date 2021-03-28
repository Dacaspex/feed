package com.dacaspex.provider.condition.string;

public class StringNotEqualCondition extends AdvancedStringCondition {
    public StringNotEqualCondition(String match) {
        super(match);
    }

    @Override
    public boolean holds(String value) {
        return !match.equalsIgnoreCase(value);
    }
}
