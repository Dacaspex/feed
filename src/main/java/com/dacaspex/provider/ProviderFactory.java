package com.dacaspex.provider;

import com.dacaspex.provider.exception.NoSuchProviderException;
import com.dacaspex.provider.nos.NosProvider;
import com.dacaspex.provider.reddit.RedditDailyTopProvider;
import com.dacaspex.provider.rocketlaunchlive.RocketLaunchLiveProvider;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.storage.event.EventStorage;
import com.google.gson.JsonObject;

public class ProviderFactory {
    private final String version;
    private final ArticleStorage articleStorage;
    private final EventStorage eventStorage;

    public ProviderFactory(String version, ArticleStorage articleStorage, EventStorage eventStorage) {
        this.version = version;
        this.articleStorage = articleStorage;
        this.eventStorage = eventStorage;
    }

    public Provider fromJson(JsonObject json) throws NoSuchProviderException {
        // TODO: Validate schema before accessing
        String type = json.get("type").getAsString();

        switch (type) {
            case "nos":
                return nosProviderFromJson(json);
            case "reddit.dailytop":
                return redditDailyTopProviderFromJson(json);
            case "rocketlaunchlive":
                return rocketLaunchLiveProviderFromJson(json);
            default:
                throw new NoSuchProviderException(type);
        }
    }

    private NosProvider nosProviderFromJson(JsonObject json) {
        return new NosProvider(
                json.get("url").getAsString(),
                articleStorage
        );
    }

    private RedditDailyTopProvider redditDailyTopProviderFromJson(JsonObject json) {
        // TODO: Validate schema before accessing

        return new RedditDailyTopProvider(
                articleStorage,
                json.get("source").getAsString(),
                version,
                json.get("username").getAsString(),
                json.get("password").getAsString(),
                json.get("clientId").getAsString(),
                json.get("clientSecret").getAsString(),
                json.get("subreddit").getAsString(),
                json.get("amount").getAsInt()
        );
    }

    private RocketLaunchLiveProvider rocketLaunchLiveProviderFromJson(JsonObject json) {
        // TODO: Validate schema before accessing

        return new RocketLaunchLiveProvider(
                json.get("url").getAsString(),
                eventStorage
        );
    }
}
