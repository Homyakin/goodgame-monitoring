package ru.homyakin.goodgame.monitoring.telegram;

import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;

@Component
public class UserController {
    private final Bot bot;
    private final Long adminId;

    public UserController(Bot bot, BotConfiguration configuration) {
        this.bot = bot;
        this.adminId = configuration.getAdminId();
    }

    public void notifyAdmin(@NotNull String text) {
        bot.send(TelegramMessageBuilder.createSendMessage(text, adminId.toString()));
    }
}
