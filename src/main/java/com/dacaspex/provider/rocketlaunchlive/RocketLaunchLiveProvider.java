package com.dacaspex.provider.rocketlaunchlive;

import com.dacaspex.collector.ItemCollector;
import com.dacaspex.collector.models.CalendarEvent;
import com.dacaspex.provider.AbstractProvider;
import com.dacaspex.provider.rocketlaunchlive.model.LaunchEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RocketLaunchLiveProvider extends AbstractProvider {
    private final static String BASE_URL = "https://fdo.rocketlaunch.live/json/launches/next/5";
    private final static Logger logger = LogManager.getLogger();

    private final Mapper mapper;

    public RocketLaunchLiveProvider(String id, ItemCollector itemCollector) {
        super(id, itemCollector);
        this.mapper = new Mapper();
    }

    @Override
    public void execute() {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
            .url(BASE_URL)
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
                itemCollector.addCalendarEvent(
                    new CalendarEvent(
                        id,
                        event.getDate(),
                        buildHeader(event),
                        buildUrl(event)
                    )
                );
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
