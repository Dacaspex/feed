package com.dacaspex.provider.rocketlaunchlive;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.rocketlaunchlive.model.LaunchEvent;
import com.dacaspex.storage.event.EventStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RocketLaunchLiveProvider implements Provider {
    private final String SOURCE = "rocketlaunchlive";
    private final String BASE_URL = "https://www.rocketlaunch.live/launch";

    private final String url;
    private final EventStorage eventStorage;
    private final Mapper mapper;

    public RocketLaunchLiveProvider(String url, EventStorage eventStorage) {
        this.url = url;
        this.eventStorage = eventStorage;
        this.mapper = new Mapper();
    }

    @Override
    public void invoke() {
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

                eventStorage.insertOrUpdateCalendarEvent(
                        event.getId(),
                        SOURCE,
                        event.getDate(),
                        buildHeader(event),
                        buildUrl(event)
                );
            });

        } catch (IOException e) {
            e.printStackTrace();
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
