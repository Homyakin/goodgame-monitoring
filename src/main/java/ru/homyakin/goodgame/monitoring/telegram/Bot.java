package ru.homyakin.goodgame.monitoring.telegram;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String username;
    private final Long adminId;

    public Bot(BotConfiguration configuration) {
        token = configuration.getToken();
        username = configuration.getUsername();
        adminId = configuration.getAdminId();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().isUserMessage()) {
                if (update.getMessage().getChatId().equals(adminId)) {
                    var message = SendMessage
                        .builder()
                        .chatId(adminId.toString())
                        .text("OK")
                        .build();
                    sendMessage(message);
                }
            }
        }
    }

    public Optional<Message> sendMessage(SendMessage message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
        }
        return Optional.empty();
    }

    public Optional<Message> sendMessage(SendPhoto message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message with photo", e);
        }
        return Optional.empty();
    }

    public Optional<Message> editMessage(EditMessageCaption message) {
        try {
            return Optional.of((Message) execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during editing message caption", e);
        }
        return Optional.empty();
    }

    public Optional<Message> editMessage(EditMessageText message) {
        try {
            return Optional.of((Message) execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during editing message text", e);
        }
        return Optional.empty();
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}