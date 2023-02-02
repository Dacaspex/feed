package com.dacaspex.provider;

import com.dacaspex.collector.ItemCollector;
import com.dacaspex.provider.exception.NoSuchProviderException;
import com.dacaspex.provider.reddit.RedditTopProvider;
import com.dacaspex.provider.reddit.RedditTopProviderSettings;
import com.dacaspex.provider.rocketlaunchlive.RocketLaunchLiveProvider;
import com.dacaspex.provider.rss.RssProvider;
import com.dacaspex.provider.rss.RssProviderSettings;
import com.dacaspex.provider.themoviedb.TheMovieDbProvider;
import com.dacaspex.provider.themoviedb.TheMovieDbProviderSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ProviderFactory {
    private final ItemCollector itemCollector;

    public ProviderFactory(ItemCollector itemCollector) {
        this.itemCollector = itemCollector;
    }

    public Provider fromJson(JsonObject json) throws NoSuchProviderException {
        // TODO: Validate schema before accessing
        String type = json.get("type").getAsString();

        switch (type) {
            case "rss":
                return rssProviderFromJson(json);
            case "reddit.top":
                return redditDailyTopProviderFromJson(json);
            case "rocketlaunchlive":
                return rocketLaunchLiveProviderFromJson(json);
            case "themoviedb":
                return theMovieDbProviderFromJson(json);
            default:
                throw new NoSuchProviderException(type);
        }
    }

    private RssProvider rssProviderFromJson(JsonObject json) {
        return new RssProvider(
            json.get("id").getAsString(),
            itemCollector,
            new Gson().fromJson(json.get("settings"), RssProviderSettings.class)
        );
    }

    private RedditTopProvider redditDailyTopProviderFromJson(JsonObject json) {
        return new RedditTopProvider(
            json.get("id").getAsString(),
            itemCollector,
            new Gson().fromJson(json.get("settings"), RedditTopProviderSettings.class)
        );
    }

    private RocketLaunchLiveProvider rocketLaunchLiveProviderFromJson(JsonObject json) {
        return new RocketLaunchLiveProvider(json.get("id").getAsString(), itemCollector);
    }

    private TheMovieDbProvider theMovieDbProviderFromJson(JsonObject json) {
        return new TheMovieDbProvider(
            json.get("id").getAsString(),
            itemCollector,
            new Gson().fromJson(json.get("settings"), TheMovieDbProviderSettings.class)
        );
    }
}
