package ru.homyakin.goodgame.monitoring.telegram;

import java.io.IOException;
import java.net.URL;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.article.models.Article;

@Component
public class TelegramMessageBuilder {

    public static EditMessageCaption createEditMessageCaption(@NotNull Message message, @NotNull Article article) {
        return EditMessageCaption
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .caption(article.toString())
            .build();
    }

    public static EditMessageText createEditMessageText(@NotNull Message message, @NotNull Article article) {
        return EditMessageText
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .text(article.toString())
            .disableWebPagePreview(true)
            .build();
    }

    public static SendMessage createSendMessageFromNews(@NotNull Article article, @NotNull String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(article.toString())
            .build();
    }

    public static SendMessage createSendMessage(@NotNull String text, @NotNull String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(text)
            .build();
    }

    public static SendPhoto creteSendPhotoFromNews(@NotNull Article article, @NotNull String chatId) throws IOException {
        return SendPhoto
            .builder()
            .photo(new InputFile(new URL(article.imageLink()).openStream(), article.link()))
            .chatId(chatId)
            .caption(article.toString())
            .build();
    }
}
