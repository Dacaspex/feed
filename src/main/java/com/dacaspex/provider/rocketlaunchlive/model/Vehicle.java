package com.dacaspex.provider.rocketlaunchlive.model;

public class Vehicle {
    private final String id;
    private final String name;

    public Vehicle(String id, String name) {
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
