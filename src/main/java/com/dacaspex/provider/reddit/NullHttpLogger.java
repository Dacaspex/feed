package com.dacaspex.provider.reddit;

import net.dean.jraw.http.HttpLogger;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * We wish to suppress the internal logging infrastructure in the Reddit Java API (JRAW). To do so,
 * we must overwrite the logger and replace it with a "null logger", i.e. this class.
 */
public class NullHttpLogger implements HttpLogger {
    @NotNull
    @Override
    public Tag request(@NotNull HttpRequest httpRequest, @NotNull Date date) {
        // I have no idea what this method does, and why it should return a tag, but
        // using the hashcode as id is a valid workaround I believe.
        return new Tag(httpRequest.hashCode(), date);
    }

    @Override
    public void response(@NotNull Tag tag, @NotNull HttpResponse httpResponse) {
        // Intentionally left blank
    }
}
