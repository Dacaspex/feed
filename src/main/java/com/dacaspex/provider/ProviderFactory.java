package com.dacaspex.provider;

import com.dacaspex.provider.exception.NoSuchProviderException;
import com.dacaspex.provider.nos.NosProvider;
import com.dacaspex.provider.reddit.RedditTopProvider;
import com.dacaspex.provider.reddit.Sort;
import com.dacaspex.provider.reddit.TimePeriod;
import com.dacaspex.provider.rocketlaunchlive.RocketLaunchLiveProvider;
import com.dacaspex.provider.tweakers.TweakersProvider;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.storage.event.EventStorage;
import com.dacaspex.storage.list.TemporaryRankedListStorage;
import com.dacaspex.util.common.Pair;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ProviderFactory {
    private final String version;
    private final ArticleStorage articleStorage;
    private final TemporaryRankedListStorage rankedListStorage;
    private final EventStorage eventStorage;

    public ProviderFactory(
        String version,
        ArticleStorage articleStorage,
        TemporaryRankedListStorage rankedListStorage,
        EventStorage eventStorage
    ) {
        this.version = version;
        this.articleStorage = articleStorage;
        this.rankedListStorage = rankedListStorage;
        this.eventStorage = eventStorage;
    }

    public Provider fromJson(JsonObject json) throws NoSuchProviderException {
        // TODO: Validate schema before accessing
        String type = json.get("type").getAsString();

        switch (type) {
            case "nos":
                return nosProviderFromJson(json);
            case "reddit":
                return redditDailyTopProviderFromJson(json);
            case "rocketlaunchlive":
                return rocketLaunchLiveProviderFromJson(json);
            case "tweakers":
                return tweakersProviderFromJson(json);
            default:
                throw new NoSuchProviderException(type);
        }
    }

    private NosProvider nosProviderFromJson(JsonObject json) {
        return new NosProvider(
            json.get("name").getAsString(),
            json.get("url").getAsString(),
            articleStorage
        );
    }

    private RedditTopProvider redditDailyTopProviderFromJson(JsonObject json) {
        // TODO: Validate schema before accessing

        return new RedditTopProvider(
            json.get("name").getAsString(),
            rankedListStorage,
            json.get("source").getAsString(),
            version,
            json.get("username").getAsString(),
            json.get("password").getAsString(),
            json.get("clientId").getAsString(),
            json.get("clientSecret").getAsString(),
            json.get("subreddit").getAsString(),
            json.get("amount").getAsInt(),
            TimePeriod.fromString(json.get("timePeriod").getAsString()),
            Sort.fromString(json.get("sort").getAsString())
        );
    }

    private RocketLaunchLiveProvider rocketLaunchLiveProviderFromJson(JsonObject json) {
        // TODO: Validate schema before accessing

        return new RocketLaunchLiveProvider(
            json.get("name").getAsString(),
            json.get("url").getAsString(),
            eventStorage
        );
    }

    private TweakersProvider tweakersProviderFromJson(JsonObject json) {
        List<Pair<String, String>> types = new ArrayList<>();

        json.get("topicWhitelist").getAsJsonArray().forEach(e -> {
            JsonObject typeObject = e.getAsJsonObject();
            types.add(
                new Pair<>(
                    typeObject.get("type").getAsString(),
                    typeObject.get("subtype").getAsString()
                )
            );
        });

        return new TweakersProvider(
            json.get("name").getAsString(),
            articleStorage,
            types
        );
    }
}
