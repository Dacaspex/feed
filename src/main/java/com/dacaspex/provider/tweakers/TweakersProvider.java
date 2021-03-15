package com.dacaspex.provider.tweakers;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.provider.tweakers.exception.UnexpectedSchemaException;
import com.dacaspex.storage.article.ArticleStorage;
import com.dacaspex.util.common.Pair;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweakersProvider implements Provider {
    private static final String NEWS_URL = "https://tweakers.net/";
    private static final String SOURCE = "tweakers";

    private final static Logger logger = LogManager.getLogger();

    private final String name;
    private final ArticleStorage articleStorage;
    private final Mapper mapper;
    private final List<Pair<String, String>> typeWhitelist;

    public TweakersProvider(String name, ArticleStorage articleStorage, List<Pair<String, String>> typeWhitelist) {
        this.name = name;
        this.articleStorage = articleStorage;
        this.mapper = new Mapper();
        this.typeWhitelist = typeWhitelist;
    }

    @Override
    public RunnableType getRunnableType() {
        return RunnableType.ANYTIME;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        try (final WebClient webClient = new WebClient()) {
            // This is not nice, but it suppresses errors and warning
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setJavaScriptEnabled(false);

            // To access the main page, we must accept cookies
            HtmlPage cookiePage = webClient.getPage(NEWS_URL);
            HtmlButton button = cookiePage.getBody().getOneHtmlElementByAttribute("button", "name", "decision");
            HtmlPage mainPage = button.click();

            // We want to select all the news items
            ArrayList<Object> elements = (ArrayList<Object>) mainPage.getByXPath("//tr[contains(@class, 'headline')]");
            for (Object o : elements) {
                HtmlElement e = (HtmlElement) o;

                try {
                    HeadlineItem item = mapper.mapElementToHeadlineItem(e);

                    if (!matchesWhitelist(item)) {
                        continue;
                    }

                    articleStorage.insertOrUpdateArticle(
                        item.getId(),
                        SOURCE,
                        item.getTitle(),
                        "",
                        item.getUrl(),
                        item.getPublishedAt()
                    );
                } catch (UnexpectedSchemaException ex) {
                    logger.error(ex);
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private boolean matchesWhitelist(HeadlineItem item) {
        return typeWhitelist.stream().anyMatch(p ->
            item.getType().equalsIgnoreCase(p.getKey()) && item.getSubType().equalsIgnoreCase(p.getValue())
        );
    }
}
