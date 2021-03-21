package com.dacaspex.provider.condition;

import java.util.List;

public class Clause<T> {
    private final List<Condition<T>> conditions;

    public Clause(List<Condition<T>> conditions) {
        this.conditions = conditions;
    }

    public boolean holds(T value) {
        return conditions.stream().allMatch(c -> c.holds(value));
    }
}
