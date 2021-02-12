package com.dacaspex.command;

import com.dacaspex.provider.Provider;

import java.util.List;

public class RunProvidersCommand {
    private final List<Provider> providers;

    public RunProvidersCommand(List<Provider> providers) {
        this.providers = providers;
    }

    public void run() {
        for (Provider provider : providers) {
            provider.invoke();
        }
    }
}
