package ru.homyakin.goodgame.monitoring.telegram;

import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.TelegramEditingError;
import ru.homyakin.goodgame.monitoring.models.TelegramError;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;

@Component
public class TelegramSender extends DefaultAbsSender {
    private static final Logger logger = LoggerFactory.getLogger(TelegramSender.class);

    protected TelegramSender(BotConfiguration botConfig, DefaultBotOptions options) {
        super(options, botConfig.getToken());
    }

    public Either<EitherError, Message> send(SendMessage message) {
        try {
            return Either.right(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
            return Either.left(new TelegramError("Unable to send message\n" + CommonUtils.getStringStackTrace(e)));
        }
    }

    public Either<EitherError, Message> send(SendPhoto message) {
        try {
            return Either.right(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message with photo", e);
            return Either.left(new TelegramError("Unable to send message `" + message.getCaption() + "` with photo\n" + CommonUtils.getStringStackTrace(e)));
        }
    }

    public Either<EitherError, Message> send(SendAnimation message) {
        try {
            return Either.right(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message with animation", e);
            return Either.left(new TelegramError("Unable to send message with animation\n" + CommonUtils.getStringStackTrace(e)));
        }
    }

    public Either<EitherError, Message> edit(EditMessageCaption message) {
        try {
            return Either.right((Message) execute(message));
        } catch (TelegramApiException e) {
            if (e.getMessage().contains(TelegramEditingError.TELEGRAM_ERROR_MESSAGE)) {
                return Either.left(new TelegramEditingError("New caption was: " + message.getCaption()));
            }
            logger.error("Something went wrong during editing message caption", e);
            return Either.left(new TelegramError("Unable to edit message caption\n" + CommonUtils.getStringStackTrace(e)));
        }
    }

    public Either<EitherError, Message> edit(EditMessageText message) {
        try {
            return Either.right((Message) execute(message));
        } catch (TelegramApiException e) {
            if (e.getMessage().contains(TelegramEditingError.TELEGRAM_ERROR_MESSAGE)) {
                return Either.left(new TelegramEditingError("New text was: " + message.getText()));
            }
            logger.error("Something went wrong during editing message text", e);
            return Either.left(new TelegramError("Unable to edit message text\n" + CommonUtils.getStringStackTrace(e)));
        }
    }
}
