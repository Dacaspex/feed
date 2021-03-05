package com.dacaspex.provider;

public interface Provider {
    RunnableType getRunnableType();

    String getName();

    void execute();
}
