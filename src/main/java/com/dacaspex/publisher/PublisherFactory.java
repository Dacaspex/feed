package com.dacaspex.publisher;

import com.dacaspex.publisher.telegram.TelegramPublisher;
import com.dacaspex.publisher.telegram.TelegramPublisherSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PublisherFactory {
    public AbstractPublisher fromJson(JsonObject json) {
        String type = json.get("type").getAsString();

        switch (type) {
            case "telegram":
                return telegramPublisherFromJson(json);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private TelegramPublisher telegramPublisherFromJson(JsonObject json) {
        return new TelegramPublisher(
            json.get("feedId").getAsString(),
            new Gson().fromJson(json.get("settings"), TelegramPublisherSettings.class)
        );
    }
}
