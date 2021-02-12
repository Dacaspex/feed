package com.dacaspex.provider.rocketlaunchlive.model;

public class Provider {
    private final String id;
    private final String name;

    public Provider(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
