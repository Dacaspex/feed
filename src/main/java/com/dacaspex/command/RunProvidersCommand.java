package com.dacaspex.command;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command to run all "anytime" providers
 */
public class RunProvidersCommand {
    private final static Logger logger = LogManager.getLogger();

    private final List<Provider> providers;

    public RunProvidersCommand(List<Provider> providers) {
        this.providers = providers;
    }

    public void run() {
        for (Provider provider : providers) {
            if (provider.getRunnableType() == RunnableType.AD_HOC) {
                logger.info("Skipping provider '{}': configured to run ad hoc", provider.getName());
                continue;
            }

            logger.info("Running provider '{}'", provider.getName());
            provider.execute();
        }
    }
}
