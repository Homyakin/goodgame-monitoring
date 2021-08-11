package ru.homyakin.goodgame.monitoring.telegram;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.news.models.News;

@Component
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final Bot bot;
    private final String channel;

    public ChannelController(Bot bot, BotConfiguration botConfiguration) {
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
    }

    public Optional<Message> sendNews(News news, @Nullable Message message) {
        try {
            if (message != null) {
                return updateMessage(news, message);
            } else {
                return bot.sendMessage(TelegramMessageBuilder.creteSendPhotoFromNews(news, channel));
            }
        } catch (IOException e) {
            logger.error("Error during sending photo", e);
            return bot.sendMessage(TelegramMessageBuilder.createMessageFromNews(news, channel));
        }
    }

    private Optional<Message> updateMessage(News news, Message message) {
        if (message.getCaption() != null) {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageCaption(message, news));
        } else {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageText(message, news));
        }
    }
}
