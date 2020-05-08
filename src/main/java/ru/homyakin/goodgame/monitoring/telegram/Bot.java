package ru.homyakin.goodgame.monitoring.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
                    var message = new SendMessage()
                        .setChatId(adminId)
                        .setText("OK");
                    sendMessage(message);
                }
            }
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
        }
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