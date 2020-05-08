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

    public Bot(BotConfiguration configuration) {
        token = configuration.getToken();
        username = configuration.getUsername();
    }


    @Override
    public void onUpdateReceived(Update update) {

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
        return token;
    }

    @Override
    public String getBotToken() {
        return username;
    }
}