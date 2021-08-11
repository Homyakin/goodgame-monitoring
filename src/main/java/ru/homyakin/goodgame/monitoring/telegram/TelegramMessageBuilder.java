package ru.homyakin.goodgame.monitoring.telegram;

import java.io.IOException;
import java.net.URL;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.news.models.News;

@Component
public class TelegramMessageBuilder {

    public static EditMessageCaption createEditMessageCaption(Message message, News news) {
        return EditMessageCaption
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .caption(news.toString())
            .build();
    }

    public static EditMessageText createEditMessageText(Message message, News news) {
        return EditMessageText
            .builder()
            .chatId(message.getChatId().toString())
            .messageId(message.getMessageId())
            .text(news.toString())
            .disableWebPagePreview(true)
            .build();
    }

    public static SendMessage createMessageFromNews(News news, String chatId) {
        return SendMessage.builder()
            .chatId(chatId)
            .disableWebPagePreview(true)
            .text(news.toString())
            .build();
    }

    public static SendPhoto creteSendPhotoFromNews(News news, String chatId) throws IOException {
        return SendPhoto
            .builder()
            .photo(new InputFile(new URL(news.getImageLink()).openStream(), news.getLink()))
            .chatId(chatId)
            .caption(news.toString())
            .build();
    }
}
