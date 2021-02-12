package com.dacaspex.publisher.telegram;

import com.dacaspex.feed.Feed;
import com.dacaspex.feed.panel.ArticlePanel;
import com.dacaspex.feed.panel.CalendarEventsPanel;
import com.dacaspex.feed.panel.Panel;
import com.dacaspex.publisher.Publisher;
import com.dacaspex.storage.article.Article;
import com.dacaspex.storage.event.CalendarEvent;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Publisher for the chat application Telegram. We are using the java-telegram-bot-api by Pengrad
 * for the interface between us and the Telegram API.
 *
 * @see <a href="https://core.telegram.org/bots">Telegram API</a>
 * @see <a href="https://github.com/pengrad/java-telegram-bot-api">java-telegram-bot-api</a>
 */
public class TelegramPublisher implements Publisher {
    private String token;
    private String chatId;
    private TelegramBot bot;

    public TelegramPublisher(String token, String chatId) {
        this.token = token;
        this.chatId = chatId;
    }

    public void init() {
        bot = new TelegramBot(token);
    }

    public void publish(Feed feed) {
        for (Panel panel : feed.getPanels()) {
            String content = null;

            // Convert each panel type to it's (Telegram specific) string equivalent
            if (panel instanceof ArticlePanel) {
                content = mapArticlePanelToString((ArticlePanel) panel);
            }

            if (panel instanceof CalendarEventsPanel) {
                content = mapCalendarEventPanelToString((CalendarEventsPanel) panel);
            }

            // Attempt to send the request
            // TODO: We are not checking for exceptions here, point for improvement
            SendMessage request = new SendMessage(chatId, content)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true);

            bot.execute(request);

            // TODO: Check response for Response.isOk()
        }
    }

    /**
     * Converts a ArticlePanel to its (Telegram specific) string equivalent
     */
    private String mapArticlePanelToString(ArticlePanel panel) {
        // TODO: Create different display when there are no articles

        // We can use some HTML tags in the string, see https://core.telegram.org/bots/api#html-style
        String header = String.format("<b>%s</b>", panel.getHeader());
        StringBuilder articles = new StringBuilder();

        for (Article article : panel.getArticles()) {
            articles.append(mapArticleToString(article));
        }

        return String.format("%s\n%s", header, articles.toString());
    }

    private String mapArticleToString(Article article) {
        // We split up the date and time to make sure the divider "|" is not rendered in italics
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd-MM");
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

        // We can use some HTML tags in the string, see https://core.telegram.org/bots/api#html-style
        return String.format(
                "•  <i>%s</i> | <i>%s</i> <a href=\"%s\">%s</a> \n",
                dateFormat.print(article.getPublishedAt()),
                timeFormat.print(article.getPublishedAt()),
                article.getLink(),
                article.getTitle()
        );
    }

    private String mapCalendarEventPanelToString(CalendarEventsPanel panel) {
        String header = String.format("<b>%s</b>", panel.getHeader());
        StringBuilder events = new StringBuilder();

        for (CalendarEvent event : panel.getEvents()) {
            events.append(mapCalendarEventToString(event));
        }

        return String.format("%s\n%s", header, events.toString());
    }

    private String mapCalendarEventToString(CalendarEvent event) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMM");

        return String.format(
                "•  %s - %s [<a href=\"%s\">link</a>]\n",
                dtf.print(event.getDate()),
                event.getHeader(),
                event.getUrl()
        );
    }
}
