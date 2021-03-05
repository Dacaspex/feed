package com.dacaspex.feed;

import com.dacaspex.feed.panel.PanelDescriptor;
import com.dacaspex.feed.panel.PanelType;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class PanelDescriptorFactory {
    public PanelDescriptor fromJson(JsonObject json) {
        List<String> sources = new ArrayList<>();

        json.getAsJsonArray("sources").forEach(e1 -> sources.add(e1.getAsString()));

        return new PanelDescriptor(
            json.get("name").getAsString(),
            json.get("header").getAsString(),
            PanelType.valueOf(json.get("display").getAsString().toUpperCase()),
            sources
        );
    }
}
