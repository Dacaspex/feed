package com.dacaspex.feed;

import com.dacaspex.feed.panel.PanelDescriptor;
import com.dacaspex.feed.panel.PanelType;
import com.dacaspex.util.common.IntervalParser;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class PanelDescriptorFactory {
    private final IntervalParser intervalParser;

    public PanelDescriptorFactory() {
        this.intervalParser = new IntervalParser();
    }

    public PanelDescriptor fromJson(JsonObject json) {
        List<String> sources = new ArrayList<>();
        json.getAsJsonArray("sources").forEach(e1 -> sources.add(e1.getAsString()));

        return new PanelDescriptor(
            json.get("name").getAsString(),
            json.get("header").getAsString(),
            PanelType.valueOf(json.get("display").getAsString().toUpperCase()),
            sources,
            intervalFromJson(json.get("relevanceInterval").getAsJsonObject())
        );
    }

    private Interval intervalFromJson(JsonObject json) {
        DateTime now = new DateTime();

        return intervalParser.centeredDayInterval(
            now,
            json.get("start").getAsInt(),
            json.get("end").getAsInt()
        );
    }
}
