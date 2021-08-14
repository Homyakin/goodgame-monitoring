package ru.homyakin.goodgame.monitoring.telegram;

import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.article.models.Article;

@Component
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final Bot bot;
    private final String channel;

    public ChannelController(Bot bot, BotConfiguration botConfiguration) {
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
    }

    public Optional<Message> sendArticle(Article article, @Nullable Message message) {
        try {
            if (message != null) {
                return updateMessage(article, message);
            } else {
                return bot.sendMessage(TelegramMessageBuilder.creteSendPhotoFromNews(article, channel));
            }
        } catch (IOException e) {
            logger.error("Error during sending photo", e);
            return bot.sendMessage(TelegramMessageBuilder.createMessageFromNews(article, channel));
        }
    }

    private Optional<Message> updateMessage(Article article, Message message) {
        if (message.getCaption().equals(article.toString())) {
            logger.info("Article {} is not required to be updated", article.getLink());
            return Optional.of(message);
        }
        logger.info("Updating {} for new text: {}", article.getLink(), article.toString());
        if (message.getCaption() != null) {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageCaption(message, article));
        } else {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageText(message, article));
        }
    }
}
