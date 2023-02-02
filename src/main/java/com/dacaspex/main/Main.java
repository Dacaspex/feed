package com.dacaspex.main;

import com.dacaspex.collector.ItemCollector;
import com.dacaspex.feed.Feed;
import com.dacaspex.feed.FeedFactory;
import com.dacaspex.provider.Provider;
import com.dacaspex.provider.ProviderFactory;
import com.dacaspex.provider.exception.NoSuchProviderException;
import com.dacaspex.publisher.AbstractPublisher;
import com.dacaspex.publisher.PublisherFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String DEFAULT_CONFIG_LOCATION = "config.local.json";
    private static final String OPTION_CONFIG_LOCATION = "c";

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws ParseException {
        // Build CLI options and parse incoming args
        Options options = buildCliInterface();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Load config
        String configJson = loadConfig(cmd);

        Gson gson = new Gson();
        JsonObject config = gson.fromJson(configJson, JsonObject.class);

        ItemCollector itemCollector = new ItemCollector();
        ProviderFactory providerFactory = new ProviderFactory(itemCollector);
        PublisherFactory publisherFactory = new PublisherFactory();
        FeedFactory feedFactory = new FeedFactory(itemCollector);

        // Create all providers as listed in the config
        List<Provider> providers = getProvidersFromConfig(config, providerFactory);
        List<AbstractPublisher> publishers = getPublishersFromConfig(config, publisherFactory);

        // Run all providers
        for (Provider provider : providers) {
            provider.execute();
        }

        List<Feed> feeds = getFeedsFromConfig(config, feedFactory);

        for (AbstractPublisher publisher : publishers) {
            Feed feed = feeds.stream().filter(f -> f.getId().equals(publisher.getFeedId())).findFirst().orElseThrow();
            publisher.publish(feed);
        }

        // This may look redundant, but for some reason the OkHttpClient does not terminate its ExecutorService
        // when we do not explicitly exit the program. TODO: Fix this weird behaviour
        System.exit(0);
    }

    private static List<Provider> getProvidersFromConfig(JsonObject config, ProviderFactory providerFactory) {
        List<Provider> providers = new ArrayList<>();

        config.get("providers")
            .getAsJsonArray()
            .forEach(providerJson -> {
                try {
                    providers.add(providerFactory.fromJson(providerJson.getAsJsonObject()));
                } catch (NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }
            });

        return providers;
    }

    private static List<AbstractPublisher> getPublishersFromConfig(JsonObject config, PublisherFactory publisherFactory) {
        List<AbstractPublisher> publishers = new ArrayList<>();

        config.get("publishers")
            .getAsJsonArray()
            .forEach(publisherJson -> {
                publishers.add(publisherFactory.fromJson(publisherJson.getAsJsonObject()));
            });

        return publishers;
    }

    private static List<Feed> getFeedsFromConfig(JsonObject config, FeedFactory feedFactory) {
        List<Feed> feeds = new ArrayList<>();

        config.get("feeds")
            .getAsJsonArray()
            .forEach(feedJson -> {
                try {
                    feeds.add(feedFactory.fromJson(feedJson.getAsJsonObject()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        return feeds;
    }

    private static String loadConfig(CommandLine cmd) {
        String configLocation = cmd.hasOption(OPTION_CONFIG_LOCATION)
            ? cmd.getOptionValue(OPTION_CONFIG_LOCATION)
            : DEFAULT_CONFIG_LOCATION;
        logger.info("Using configuration file '{}'", configLocation);

        try {
            return getConfig(configLocation);
        } catch (IOException e) {
            logger.fatal("Could not load configuration file: {}", e.getMessage());

            System.exit(-1);
            return "";
        }
    }

    private static Options buildCliInterface() {
        Options options = new Options();

        options.addOption(OPTION_CONFIG_LOCATION, "config", true, "Location of config file");

        return options;
    }

    private static String getConfig(final String path) throws IOException {
        return Files.readString(Paths.get(path));
    }
}
