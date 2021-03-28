package com.dacaspex.provider.rocketlaunchlive.condition;

import com.dacaspex.provider.condition.Condition;
import com.dacaspex.provider.rocketlaunchlive.model.LaunchEvent;

public class ProviderNameCondition implements Condition<LaunchEvent> {
    private final String name;

    public ProviderNameCondition(String name) {
        this.name = name;
    }

    @Override
    public boolean holds(LaunchEvent value) {
        return value.getProvider().getName().equalsIgnoreCase(name);
    }
}
