package com.dacaspex.main;

import com.dacaspex.command.CommandFactory;
import com.dacaspex.command.RunProvidersCommand;
import com.dacaspex.command.RunPublisherCommand;
import com.dacaspex.exception.InvalidSchemaException;
import com.dacaspex.feed.PanelDescriptorFactory;
import com.dacaspex.provider.ProviderFactory;
import com.dacaspex.publisher.PublisherFactory;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.storage.event.EventStorage;
import com.dacaspex.storage.list.TemporaryRankedListStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String VERSION = "v0.1.0";
    private static final String DEFAULT_CONFIG_LOCATION = "config.local.json";
    private static final String OPTION_RUN_PROVIDERS = "i";
    private static final String OPTION_RUN_PUBLISHERS = "o";

    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws ParseException {
        logger.info("Launching application. Configuring...");

        // Build command line options and parse incoming args
        Options options = buildCliInterface();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Load config
        String json = null;
        try {
            json = getConfig(DEFAULT_CONFIG_LOCATION);
        } catch (IOException e) {
            logger.fatal("Could not load configuration file: {}", e.getMessage());

            System.exit(-1);
        }

        // Run program setup. This is normally done via Dependency Injection but I haven't set that up yet
        // TODO: Setup Dependency Injection
        Gson gson = new Gson();
        JsonObject config = gson.fromJson(json, JsonObject.class);
        ArticleStorage articleStorage = new ArticleStorage(
            config.get("storage").getAsJsonObject().get("host").getAsString(),
            config.get("storage").getAsJsonObject().get("name").getAsString(),
            config.get("storage").getAsJsonObject().get("username").getAsString(),
            config.get("storage").getAsJsonObject().get("password").getAsString()
        );
        EventStorage eventStorage = new EventStorage(
            config.get("storage").getAsJsonObject().get("host").getAsString(),
            config.get("storage").getAsJsonObject().get("name").getAsString(),
            config.get("storage").getAsJsonObject().get("username").getAsString(),
            config.get("storage").getAsJsonObject().get("password").getAsString()
        );
        TemporaryRankedListStorage rankedListStorage = new TemporaryRankedListStorage();
        ProviderFactory providerFactory = new ProviderFactory(VERSION, articleStorage, rankedListStorage, eventStorage);
        PanelDescriptorFactory panelDescriptorFactory = new PanelDescriptorFactory();
        PublisherFactory publisherFactory = new PublisherFactory();
        CommandFactory commandFactory = new CommandFactory(
            articleStorage,
            eventStorage,
            rankedListStorage,
            providerFactory,
            publisherFactory,
            panelDescriptorFactory
        );

        // Configure commands
        RunProvidersCommand runProvidersCommand;
        RunPublisherCommand runPublisherCommand;
        try {
            runProvidersCommand = commandFactory.getRunProvidersCommandFromJson(config);
            runPublisherCommand = commandFactory.getRunPublisherCommandFromJson(config);
        } catch (InvalidSchemaException e) {
            logger.fatal(e);
            System.exit(-1);
            return;
        }

        logger.info("Configured, ready to parse commands");

        // Determine command branch execution. Multiple branches may be executed in one run
        boolean hasExecutedCommand = false;
        if (cmd.hasOption(OPTION_RUN_PROVIDERS)) {
            logger.info("Executing run providers command...");
            runProvidersCommand.run();
            hasExecutedCommand = true;
        }

        if (cmd.hasOption(OPTION_RUN_PUBLISHERS)) {
            logger.info("Executing run publishers command...");
            runPublisherCommand.run();
            hasExecutedCommand = true;
        }

        if (!hasExecutedCommand) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java <program name>", options);
        }

        logger.info("Exiting... Bye \uD83D\uDC4B");

        // This may look redundant, but for some reason the OkHttpClient does not terminate its ExecutorService
        // when we do not explicitly exit the program. TODO: Fix this weird behaviour
        System.exit(0);
    }

    private static Options buildCliInterface() {
        Options options = new Options();

        options.addOption(OPTION_RUN_PROVIDERS, "run-providers", false, "Run content providers");
        options.addOption(OPTION_RUN_PUBLISHERS, "run-publishers", false, "Run publishers");

        return options;
    }

    private static String getConfig(final String path) throws IOException {
        return Files.readString(Paths.get(path));
    }
}
