package ru.homyakin.goodgame.monitoring.telegram;

import io.vavr.control.Either;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.ApiArticle;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.SavedMessage;
import ru.homyakin.goodgame.monitoring.models.TelegramEditingError;
import ru.homyakin.goodgame.monitoring.models.TelegramError;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Component
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final TelegramSender telegramSender;
    private final String channel;
    private final UserController userController;

    public ChannelController(
        TelegramSender telegramSender,
        BotConfiguration botConfiguration,
        UserController userController
    ) {
        this.telegramSender = telegramSender;
        this.channel = botConfiguration.getChannel();
        this.userController = userController;
    }

    public Either<EitherError, Message> sendArticle(@NotNull Article article) {
        try {
            logger.info("Sending new article {}", article.link());
            return switch (article.getMediaType()) {
                case IMAGE -> telegramSender.send(TelegramMessageBuilder.creteSendPhotoFromArticle(article, channel));
                case GIF -> telegramSender.send(TelegramMessageBuilder.createSendAnimationFromArticle(article, channel));
            };
        } catch (IOException e) {
            if (article.isTournament()) {
                return Either.left(new TelegramError("Error sending photo to tournament " + article.link()));
            } else {
                logger.error("Error during sending photo", e);
                userController.notifyAdmin("Error during sending photo\n" + CommonUtils.getStringStackTrace(e));
                return telegramSender.send(TelegramMessageBuilder.createSendMessageFromArticle(article, channel));
            }
        }
    }

    public Either<EitherError, Message> updateMessage(@NotNull Article article, @NotNull SavedMessage savedMessage) {
        if (article.toMessageText().equals(savedMessage.sentText())) {
            return Either.right(savedMessage.message());
        }
        final Either<EitherError, Message> result;
        if (savedMessage.message().getCaption() != null) {
            logger.info("Updating {} for new caption: {}", article.link(), article.toMessageText());
            result = telegramSender.edit(TelegramMessageBuilder.createEditMessageCaptionFromArticle(savedMessage.message(), article));
        } else {
            logger.info("Updating {} for new text: {}", article.link(), article.toMessageText());
            result = telegramSender.edit(TelegramMessageBuilder.createEditMessageTextFromArticle(savedMessage.message(), article));
        }
        if (result.isLeft() && result.getLeft() instanceof TelegramEditingError) {
            logger.error(
                String.format("Error during editing old text %s to new %s", savedMessage.sentText(), article.toMessageText())
            );
        }
        return result;
    }

    public void sendArticles(List<ArticleInfo> articleInfos, long startDate, long endDate) {

        var start = DateTimeUtils.longToMoscowDateTime(startDate);
        var end = DateTimeUtils.longToMoscowDateTime(endDate).minus(1, ChronoUnit.DAYS);
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        StringBuilder text = new StringBuilder(String.format(
            "Топ %d новостей с %s по %s:\n\n",
            articleInfos.size(),
            start.format(formatter),
            end.format(formatter)
        ));

        for (int i = 0; i < articleInfos.size(); ++i) {
            text.append(i + 1).append(") ").append(articleInfos.get(i).toTelegramText()).append("\n");
        }

        var result = telegramSender.send(TelegramMessageBuilder.createSendMessageWithHtmlParseMode(text.toString(), channel));

        if (result.isLeft()) {
            userController.notifyAdmin(result.getLeft().getMessage());
        }
    }
}
