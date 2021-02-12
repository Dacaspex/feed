package com.dacaspex.publisher;

import com.dacaspex.publisher.telegram.TelegramPublisher;
import com.google.gson.JsonObject;

public class PublisherFactory {
    public Publisher fromJson(JsonObject json) {
        String type = json.get("type").getAsString();

        switch (type) {
            case "telegram":
                // A Telegram bot requires an API token. Read more here: https://core.telegram.org/bots
                // Moreover, we also need the chat id to know to which chat to push the feed
                String token = json.get("token").getAsString();
                String chatId = json.get("chat-id").getAsString();

                TelegramPublisher telegramPublisher = new TelegramPublisher(token, chatId);
                telegramPublisher.init();

                return telegramPublisher;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
