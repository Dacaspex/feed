package com.dacaspex.provider.rocketlaunchlive;

import com.dacaspex.provider.rocketlaunchlive.model.LaunchEvent;
import com.dacaspex.provider.rocketlaunchlive.model.Provider;
import com.dacaspex.provider.rocketlaunchlive.model.Vehicle;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;

public class Mapper {
    public LaunchEvent mapJsonToLaunchEvent(JsonObject json) {
        return new LaunchEvent(
                json.get("id").getAsString(),
                json.get("name").getAsString(),
                json.get("slug").getAsString(),
                mapJsonToUnreliableDateTime(json),
                mapJsonToProvider(json.get("provider").getAsJsonObject()),
                mapJsonToVehicle(json.get("vehicle").getAsJsonObject())
        );
    }

    public Provider mapJsonToProvider(JsonObject json) {
        return new Provider(
                json.get("id").getAsString(),
                json.get("name").getAsString()
        );
    }

    public Vehicle mapJsonToVehicle(JsonObject json) {
        return new Vehicle(
                json.get("id").getAsString(),
                json.get("name").getAsString()
        );
    }

    private DateTime mapJsonToUnreliableDateTime(JsonObject json) {
        // TODO: The "sort_date" might not be accurate, use "est_date" too
        return new DateTime(json.get("sort_date").getAsLong() * 1000L);
    }
}
