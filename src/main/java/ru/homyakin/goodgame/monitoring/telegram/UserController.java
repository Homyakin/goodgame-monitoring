package ru.homyakin.goodgame.monitoring.telegram;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;

@Component
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final TelegramSender telegramSender;
    private final Long adminId;

    public UserController(TelegramSender telegramSender, BotConfiguration configuration) {
        this.telegramSender = telegramSender;
        this.adminId = configuration.getAdminId();
    }

    public void notifyAdmin(@Nonnull String text) {
        logger.info("Sending message to admin");
        telegramSender.send(TelegramMessageBuilder.createSendMessage(text, adminId.toString()));
    }
}
