package ru.homyakin.goodgame.monitoring.telegram;

import io.vavr.control.Either;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.article.models.Article;
import ru.homyakin.goodgame.monitoring.models.EitherError;

@Component
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final Bot bot;
    private final String channel;

    public ChannelController(Bot bot, BotConfiguration botConfiguration) {
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
    }

    public Either<EitherError, Message> sendArticle(@NotNull Article article, @Nullable Message message) {
        try {
            if (message != null) {
                return updateMessage(article, message);
            } else {
                return bot.sendMessage(TelegramMessageBuilder.creteSendPhotoFromNews(article, channel));
            }
        } catch (IOException e) {
            logger.error("Error during sending photo", e);
            return bot.sendMessage(TelegramMessageBuilder.createSendMessageFromNews(article, channel));
        }
    }

    private Either<EitherError, Message> updateMessage(@NotNull Article article, @NotNull Message message) {
        if (article.toString().equals(message.getCaption())) {
            logger.info("Article {} is not required to be updated", article.getLink());
            return Either.right(message);
        }
        logger.info("Updating {} for new text: {}", article.getLink(), article.toString());
        if (message.getCaption() != null) {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageCaption(message, article));
        } else {
            return bot.editMessage(TelegramMessageBuilder.createEditMessageText(message, article));
        }
    }
}
