package com.dacaspex.provider.condition.string;

public class StringEqualCondition extends AdvancedStringCondition {
    public StringEqualCondition(String match) {
        super(match);
    }

    @Override
    public boolean holds(String value) {
        return match.equalsIgnoreCase(value);
    }
}
