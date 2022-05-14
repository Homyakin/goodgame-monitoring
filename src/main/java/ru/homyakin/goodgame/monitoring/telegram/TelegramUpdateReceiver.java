package ru.homyakin.goodgame.monitoring.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import ru.homyakin.goodgame.monitoring.config.BotConfiguration;
import ru.homyakin.goodgame.monitoring.service.ArticleMonitoring;

@Component
public class TelegramUpdateReceiver implements LongPollingBot {
    private final String token;
    private final String username;
    private final Long adminId;
    private final DefaultBotOptions botOptions;
    private final TelegramSender telegramSender;
    private final ArticleMonitoring articleMonitoring;

    public TelegramUpdateReceiver(
        BotConfiguration configuration,
        DefaultBotOptions botOptions,
        TelegramSender telegramSender,
        ArticleMonitoring articleMonitoring
    ) {
        token = configuration.getToken();
        username = configuration.getUsername();
        adminId = configuration.getAdminId();
        this.botOptions = botOptions;
        this.telegramSender = telegramSender;
        this.articleMonitoring = articleMonitoring;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().isUserMessage()) {
                if (update.getMessage().getChatId().equals(adminId)) {
                    if (update.getMessage().hasText() && update.getMessage().getText().equals("/week")){
                        articleMonitoring.findTopWeekArticles();
                    }
                    final var message = TelegramMessageBuilder.createSendMessage("OK", adminId.toString());
                    telegramSender.send(message);
                }
            }
        }
    }

    @Override
    public BotOptions getOptions() {
        return botOptions;
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {

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