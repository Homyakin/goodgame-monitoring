package ru.homyakin.goodgame.monitoring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.homyakin.goodgame.monitoring.telegram.TelegramUpdateReceiver;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {
    private final TelegramUpdateReceiver bot;

    public Application(TelegramUpdateReceiver bot) {
        this.bot = bot;
    }

    @Override
    public void run(String... args) throws Exception {
        final var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
