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

    public Either<EitherError, Message> sendArticle(@NotNull Article article, @Nullable Message message) {
        try {
            if (message != null) {
                return updateMessage(article, message);
            } else {
                return switch (article.getMediaType()) {
                    case IMAGE -> bot.send(TelegramMessageBuilder.creteSendPhotoFromArticle(article, channel));
                    case GIF -> bot.send(TelegramMessageBuilder.createSendAnimationFromArticle(article, channel));
                };
            }
        } catch (IOException e) {
            logger.error("Error during sending photo", e);
            userController.notifyAdmin("Error during sending photo\n" + CommonUtils.getStringStackTrace(e));
            return bot.send(TelegramMessageBuilder.createSendMessageFromArticle(article, channel));
        }
    }

    private Either<EitherError, Message> updateMessage(@NotNull Article article, @NotNull Message message) {
        if (article.toString().equals(message.getCaption())) {
            logger.info("Article {} is not required to be updated", article.link());
            return Either.right(message);
        }
        logger.info("Updating {} for new text: {}", article.link(), article.toString());
        if (message.getCaption() != null) {
            return bot.edit(TelegramMessageBuilder.createEditMessageCaptionFromArticle(message, article));
        } else {
            return bot.edit(TelegramMessageBuilder.createEditMessageTextFromArticle(message, article));
        }
    }
}
