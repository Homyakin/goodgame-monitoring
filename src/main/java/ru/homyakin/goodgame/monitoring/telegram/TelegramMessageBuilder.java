package ru.homyakin.goodgame.monitoring.telegram;

import java.io.IOException;
import java.net.URL;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.models.Article;

@Component
public class TelegramMessageBuilder {
    private final static int MAX_TELEGRAM_MESSAGE = 4000;
    private final static int CAPTION_MAX_LENGTH = 1024;

    public static SendAnimation createSendAnimationFromArticle(@NotNull Article article, @NotNull String chatId) throws IOException {
        return SendAnimation
            .builder()
            .chatId(chatId)
            .caption(substringToTelegramLength(article.toMessageText(CAPTION_MAX_LENGTH)))
            .animation(new InputFile(article.mediaLink()))
            .parseMode(ParseMode.HTML)
            .build();
    }

    public static EditMessageCaption createEditMessageCaptionFromArticle(@NotNull Message message, @NotNull Article article) {
        return EditMessageCaption
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .caption(substringToTelegramLength(article.toMessageText(CAPTION_MAX_LENGTH)))
            .parseMode(ParseMode.HTML)
            .build();
    }

    public static EditMessageText createEditMessageTextFromArticle(@NotNull Message message, @NotNull Article article) {
        return EditMessageText
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .text(substringToTelegramLength(article.toMessageText()))
            .disableWebPagePreview(true)
            .parseMode(ParseMode.HTML)
            .build();
    }

    public static SendMessage createSendMessageFromArticle(@NotNull Article article, @NotNull String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(substringToTelegramLength(article.toMessageText()))
            .parseMode(ParseMode.HTML)
            .build();
    }

    public static SendMessage createSendMessage(@NotNull String text, @NotNull String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(substringToTelegramLength(text))
            .build();
    }

    public static SendMessage createSendMessageWithHtmlParseMode(@NotNull String text, @NotNull String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(substringToTelegramLength(text))
            .parseMode(ParseMode.HTML)
            .build();
    }

    public static SendPhoto creteSendPhotoFromArticle(@NotNull Article article, @NotNull String chatId) throws IOException {
        return SendPhoto
            .builder()
            .photo(new InputFile(new URL(article.mediaLink()).openStream(), article.link()))
            .chatId(chatId)
            .caption(substringToTelegramLength(article.toMessageText(CAPTION_MAX_LENGTH)))
            .parseMode(ParseMode.HTML)
            .build();
    }

    private static String substringToTelegramLength(String s) {
        return s.substring(0, Math.min(s.length(), MAX_TELEGRAM_MESSAGE));
    }
}
