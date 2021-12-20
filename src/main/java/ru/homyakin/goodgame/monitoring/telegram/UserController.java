package ru.homyakin.goodgame.monitoring.telegram;

import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;

@Component
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Bot bot;
    private final Long adminId;

    public UserController(Bot bot, BotConfiguration configuration) {
        this.bot = bot;
        this.adminId = configuration.getAdminId();
    }

    public void notifyAdmin(@NotNull String text) {
        logger.info("Sending message to admin");
        bot.send(TelegramMessageBuilder.createSendMessage(text, adminId.toString()));
    }
}
