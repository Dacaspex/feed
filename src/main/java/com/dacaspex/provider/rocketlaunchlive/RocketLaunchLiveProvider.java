package com.dacaspex.provider.rocketlaunchlive;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.provider.condition.Condition;
import com.dacaspex.provider.condition.Conditions;
import com.dacaspex.provider.rocketlaunchlive.model.LaunchEvent;
import com.dacaspex.storage.event.EventStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RocketLaunchLiveProvider implements Provider {
    private final static Logger logger = LogManager.getLogger();

    private final static String SOURCE = "rocketlaunchlive";
    private final static String BASE_URL = "https://www.rocketlaunch.live/launch";

    private final String name;
    private final String url;
    private final EventStorage eventStorage;
    private final Mapper mapper;
    private final Conditions<LaunchEvent> conditions;

    public RocketLaunchLiveProvider(String name, String url, EventStorage eventStorage, Conditions<LaunchEvent> conditions) {
        this.name = name;
        this.url = url;
        this.eventStorage = eventStorage;
        this.mapper = new Mapper();
        this.conditions = conditions;
    }

    @Override
    public RunnableType getRunnableType() {
        return RunnableType.ANYTIME;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
            .url(url)
            .build();

        Call call = httpClient.newCall(request);

        try {
            Response response = call.execute();

            if (!response.isSuccessful() || response.body() == null) {
                return;
            }

            String rawJson = response.body().string();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(rawJson, JsonObject.class);
            json.get("result").getAsJsonArray().forEach(e -> {
                LaunchEvent event = mapper.mapJsonToLaunchEvent(e.getAsJsonObject());

                if (!conditions.holds(event)) {
                    return;
                }

                System.out.println(event.getName());
                System.out.println(event.getVehicle().getName());
                System.out.println(event.getDate().toString());
                System.out.println(event.getProvider().getName());
                System.out.println();

//                eventStorage.insertOrUpdateCalendarEvent(
//                    event.getId(),
//                    SOURCE,
//                    event.getDate(),
//                    buildHeader(event),
//                    buildUrl(event)
//                );
            });

        } catch (IOException e) {
            logger.error(e);
        }
    }

    private String buildHeader(LaunchEvent event) {
        return String.format(
            "%s using %s (%s)",
            event.getName(),
            event.getVehicle().getName(),
            event.getProvider().getName()
        );
    }

    private String buildUrl(LaunchEvent event) {
        return String.format("%s/%s", BASE_URL, event.getSlug());
    }
}
