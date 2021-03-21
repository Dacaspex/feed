package com.dacaspex.provider.tweakers;

import com.dacaspex.provider.condition.Condition;

import java.util.List;

public class TitleContainsCondition implements Condition<HeadlineItem> {
    private final List<String> tokens;

    public TitleContainsCondition(List<String> tokens) {
        this.tokens = tokens;
    }

    public boolean holds(HeadlineItem item) {
        return tokens.stream().anyMatch(
            t -> item.getTitle()
                .toLowerCase()
                .contains(t.toLowerCase())
        );
    }
}
