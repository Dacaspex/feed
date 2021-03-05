package com.dacaspex.command;

import com.dacaspex.exception.InvalidSchemaException;
import com.dacaspex.feed.FeedDescriptor;
import com.dacaspex.feed.PanelDescriptorFactory;
import com.dacaspex.feed.panel.PanelDescriptor;
import com.dacaspex.provider.Provider;
import com.dacaspex.provider.ProviderFactory;
import com.dacaspex.provider.exception.NoSuchProviderException;
import com.dacaspex.publisher.Publisher;
import com.dacaspex.publisher.PublisherFactory;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.storage.event.EventStorage;
import com.dacaspex.storage.list.TemporaryRankedListStorage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {
    private final ArticleStorage articleStorage;
    private final EventStorage eventStorage;
    private final TemporaryRankedListStorage rankedListStorage;

    private final ProviderFactory providerFactory;
    private final PublisherFactory publisherFactory;
    private final PanelDescriptorFactory panelDescriptorFactory;

    public CommandFactory(
        ArticleStorage articleStorage,
        EventStorage eventStorage,
        TemporaryRankedListStorage rankedListStorage,
        ProviderFactory providerFactory,
        PublisherFactory publisherFactory,
        PanelDescriptorFactory panelDescriptorFactory
    ) {
        this.articleStorage = articleStorage;
        this.eventStorage = eventStorage;
        this.rankedListStorage = rankedListStorage;
        this.providerFactory = providerFactory;
        this.publisherFactory = publisherFactory;
        this.panelDescriptorFactory = panelDescriptorFactory;
    }

    public RunProvidersCommand getRunProvidersCommandFromJson(JsonObject json) throws InvalidSchemaException {
        List<Provider> providers = new ArrayList<>();

        // TODO: Validate schema before accessing

        for (JsonElement e : json.getAsJsonArray("providers")) {
            providers.add(providerFactory.fromJson(e.getAsJsonObject()));
        }

        return new RunProvidersCommand(providers);
    }

    public RunPublisherCommand getRunPublisherCommandFromJson(JsonObject json) throws NoSuchProviderException {
        List<FeedDescriptor> feedDescriptors = new ArrayList<>();

        // TODO: Validate schema before accessing

        json.getAsJsonArray("feeds").forEach(e1 -> {
            JsonObject feedJson = e1.getAsJsonObject();

            List<Publisher> publishers = new ArrayList<>();
            List<PanelDescriptor> panelDescriptors = new ArrayList<>();

            feedJson.getAsJsonArray("publishers").forEach(
                e2 -> publishers.add(publisherFactory.fromJson(e2.getAsJsonObject()))
            );

            feedJson.getAsJsonArray("panels").forEach(
                e2 -> panelDescriptors.add(panelDescriptorFactory.fromJson(e2.getAsJsonObject()))
            );

            feedDescriptors.add(
                new FeedDescriptor(
                    feedJson.get("name").getAsString(),
                    publishers,
                    panelDescriptors
                )
            );
        });

        List<Provider> providers = new ArrayList<>();
        for (JsonElement e : json.getAsJsonArray("providers")) {
            providers.add(providerFactory.fromJson(e.getAsJsonObject()));
        }

        return new RunPublisherCommand(articleStorage, eventStorage, rankedListStorage, feedDescriptors, providers);
    }
}
