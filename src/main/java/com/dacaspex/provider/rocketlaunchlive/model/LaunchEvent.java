package com.dacaspex.provider.rocketlaunchlive.model;

import org.joda.time.DateTime;

public class LaunchEvent {
    private final String id;
    private final String name;
    private final String slug;
    private final DateTime date;
    private final Provider provider;
    private final Vehicle vehicle;

    public LaunchEvent(String id, String name, String slug, DateTime date, Provider provider, Vehicle vehicle) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.date = date;
        this.provider = provider;
        this.vehicle = vehicle;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public DateTime getDate() {
        return date;
    }

    public Provider getProvider() {
        return provider;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
