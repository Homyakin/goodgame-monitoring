package ru.homyakin.goodgame.monitoring.telegram;

import io.vavr.control.Either;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.SavedMessage;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;

@Component
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final Bot bot;
    private final String channel;
    private final UserController userController;

    public ChannelController(
        Bot bot,
        BotConfiguration botConfiguration,
        UserController userController
    ) {
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
        this.userController = userController;
    }

    public Either<EitherError, Message> sendArticle(@NotNull Article article) {
        try {
            logger.info("Sending new article {}", article.link());
            return switch (article.getMediaType()) {
                case IMAGE -> bot.send(TelegramMessageBuilder.creteSendPhotoFromArticle(article, channel));
                case GIF -> bot.send(TelegramMessageBuilder.createSendAnimationFromArticle(article, channel));
            };
        } catch (IOException e) {
            logger.error("Error during sending photo", e);
            userController.notifyAdmin("Error during sending photo\n" + CommonUtils.getStringStackTrace(e));
            return bot.send(TelegramMessageBuilder.createSendMessageFromArticle(article, channel));
        }
    }

    public Either<EitherError, Message> updateMessage(@NotNull Article article, @NotNull SavedMessage savedMessage) {
        if (savedMessage.message().getCaption() != null) {
            return updateMessageCaption(article, savedMessage);
        } else {
            return updateTextMessage(article, savedMessage);
        }
    }

    private Either<EitherError, Message> updateMessageCaption(@NotNull Article article, @NotNull SavedMessage savedMessage) {
        if (article.toMessageText().equals(savedMessage.sentText())) {
            return Either.right(savedMessage.message());
        } else {
            logger.info("Updating {} for new caption: {}", article.link(), article.toMessageText());
            return bot.edit(TelegramMessageBuilder.createEditMessageCaptionFromArticle(savedMessage.message(), article));
        }
    }

    private Either<EitherError, Message> updateTextMessage(@NotNull Article article, @NotNull SavedMessage savedMessage) {
        if (article.toMessageText().equals(savedMessage.sentText())) {
            return Either.right(savedMessage.message());
        } else {
            logger.info("Updating {} for new text: {}", article.link(), article.toMessageText());
            return bot.edit(TelegramMessageBuilder.createEditMessageTextFromArticle(savedMessage.message(), article));
        }
    }
}
