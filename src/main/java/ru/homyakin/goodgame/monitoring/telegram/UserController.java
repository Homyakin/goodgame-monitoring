package ru.homyakin.goodgame.monitoring.telegram;

import javax.validation.constraints.NotNull;
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

    public void notifyAdmin(@NotNull String text) {
        logger.info("Sending message to admin");
        telegramSender.send(TelegramMessageBuilder.createSendMessage(text, adminId.toString()));
    }
}
