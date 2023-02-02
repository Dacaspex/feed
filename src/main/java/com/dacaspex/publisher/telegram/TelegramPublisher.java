package com.dacaspex.publisher.telegram;

import com.dacaspex.feed.Feed;
import com.dacaspex.feed.panel.ArticlePanel;
import com.dacaspex.feed.panel.CalendarEventsPanel;
import com.dacaspex.feed.panel.Panel;
import com.dacaspex.feed.panel.OrderedListPanel;
import com.dacaspex.publisher.AbstractPublisher;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * Publisher for the chat application Telegram. We are using the java-telegram-bot-api by Pengrad
 * for the interface between us and the Telegram API.
 *
 * @see <a href="https://core.telegram.org/bots">Telegram API</a>
 * @see <a href="https://github.com/pengrad/java-telegram-bot-api">java-telegram-bot-api</a>
 */
public class TelegramPublisher extends AbstractPublisher {
    public final TelegramPublisherSettings settings;
    private final Mapper mapper;

    public TelegramPublisher(String feedId, TelegramPublisherSettings settings) {
        super(feedId);
        this.settings = settings;
        this.mapper = new Mapper();
    }

    public void publish(Feed feed) {
        TelegramBot bot = new TelegramBot(settings.token);

        for (Panel panel : feed.getPanels()) {
            String content = null;

            // Convert each panel type to it's (Telegram specific) string equivalent
            if (panel instanceof ArticlePanel) {
                content = mapper.mapArticlePanelToString((ArticlePanel) panel);
            }

            if (panel instanceof CalendarEventsPanel) {
                content = mapper.mapCalendarEventsPanelToString((CalendarEventsPanel) panel);
            }

            if (panel instanceof OrderedListPanel) {
                content = mapper.mapRankedListToString((OrderedListPanel) panel);
            }

            // Attempt to send the request
            // TODO: We are not checking for exceptions here, point for improvement
            SendMessage request = new SendMessage(settings.chatId, content)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);

            bot.execute(request);

            // TODO: Check response for Response.isOk()
        }
    }
}
