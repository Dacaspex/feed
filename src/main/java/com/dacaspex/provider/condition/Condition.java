package com.dacaspex.provider.condition;

public interface Condition<T> {
    public boolean holds(T value);
}
