package com.dacaspex.provider.condition;

import java.util.List;

public class Conditions<T> {
    private final List<Clause<T>> clauses;

    public Conditions(List<Clause<T>> clauses) {
        this.clauses = clauses;
    }

    public boolean holds(T value) {
        return clauses.stream().anyMatch(c -> c.holds(value));
    }
}
