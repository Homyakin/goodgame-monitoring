package ru.homyakin.goodgame.monitoring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ru.homyakin.goodgame.monitoring.telegram.Bot;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {
    private final Bot bot;

    public Application(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run(String... args) throws Exception {
        var telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(bot);
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }
}
