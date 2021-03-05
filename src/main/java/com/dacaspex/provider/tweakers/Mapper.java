package com.dacaspex.provider.tweakers;

import com.dacaspex.provider.tweakers.exception.UnexpectedSchemaException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.joda.time.DateTime;

public class Mapper {
    public HeadlineItem mapElementToHeadlineItem(HtmlElement headlineElement) throws UnexpectedSchemaException {
        String[] types = extractTypes(headlineElement);

        return new HeadlineItem(
            extractId(headlineElement),
            types[0],
            types[1],
            extractPublishedAt(headlineElement),
            extractTitle(headlineElement),
            extractUrl(headlineElement),
            extractCommentCount(headlineElement)
        );
    }

    private String extractId(HtmlElement headlineHtml) throws UnexpectedSchemaException {
        String id;
        try {
            id = ((HtmlElement) headlineHtml.getByXPath("td[contains(@class, 'title')]/a").get(0))
                .getAttribute("href");
        } catch (Exception e) {
            throw new UnexpectedSchemaException(e);
        }

        return id;
    }

    private String[] extractTypes(HtmlElement headlineHtml) throws UnexpectedSchemaException {
        String[] types;
        try {
            types = ((HtmlElement) headlineHtml.getByXPath("td[contains(@class, 'type')]/span").get(0))
                .getAttribute("title")
                .split("\\|");
        } catch (Exception e) {
            throw new UnexpectedSchemaException(e);
        }

        if (types.length != 2) {
            throw new UnexpectedSchemaException(
                String.format(
                    "Expected to find exactly two types, found %s (%s)",
                    types.length,
                    String.join("", types)
                )
            );
        }

        return new String[]{types[0].trim(), types[1].trim()};
    }

    private DateTime extractPublishedAt(HtmlElement headlineHtml) {
        // TODO: Extract the actual published at date
        return new DateTime();
    }

    private String extractTitle(HtmlElement headlineHtml) throws UnexpectedSchemaException {
        String title;
        try {
            title = ((HtmlElement) headlineHtml.getByXPath("td[contains(@class, 'title')]/a").get(0))
                .getTextContent();
        } catch (Exception e) {
            throw new UnexpectedSchemaException(e);
        }

        return title;
    }

    private String extractUrl(HtmlElement headlineHtml) throws UnexpectedSchemaException {
        String url;
        try {
            url = ((HtmlElement) headlineHtml.getByXPath("td[contains(@class, 'title')]/a").get(0))
                .getAttribute("href");
        } catch (Exception e) {
            throw new UnexpectedSchemaException(e);
        }

        if (url.equalsIgnoreCase("")) {
            throw new UnexpectedSchemaException("Expected to find a non-empty url string");
        }

        return url;
    }

    private int extractCommentCount(HtmlElement headlineHtml) throws UnexpectedSchemaException {
        String replyCountString;
        try {
            replyCountString = ((HtmlElement) headlineHtml.getByXPath("td[contains(@class, 'replies')]/a").get(0))
                .getTextContent();
        } catch (Exception e) {
            throw new UnexpectedSchemaException(e);
        }

        if (replyCountString.equalsIgnoreCase("")) {
            throw new UnexpectedSchemaException("Expected to find a non-empty reply count string");
        }

        int replyCount;
        try {
            replyCount = Integer.parseInt(replyCountString);
        } catch (NumberFormatException e) {
            throw new UnexpectedSchemaException(
                String.format("Expected reply count to be an integer, found %s", replyCountString)
            );
        }

        return replyCount;
    }
}
