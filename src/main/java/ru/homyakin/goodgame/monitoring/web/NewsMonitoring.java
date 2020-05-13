package ru.homyakin.goodgame.monitoring.web;

import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.homyakin.goodgame.monitoring.telegram.Bot;
import ru.homyakin.goodgame.monitoring.telegram.BotConfiguration;
import ru.homyakin.goodgame.monitoring.web.models.News;

@Service
public class NewsMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(NewsMonitoring.class);
    private final NewsScanner newsScanner;
    private final Bot bot;
    private final String channel;
    private Long lastNewsDate = null;

    public NewsMonitoring(NewsScanner newsScanner, Bot bot, BotConfiguration botConfiguration) {
        this.newsScanner = newsScanner;
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitorNews() {
        var news = newsScanner.getLastNews();
        if (lastNewsDate == null) {
            logger.info("Initialized monitoring");
            lastNewsDate = news.get(0).getDate();
        }

        int lastIdx = 0;
        while (lastIdx < news.size() && lastNewsDate < news.get(lastIdx).getDate()) {
            ++lastIdx;
        }
        if (lastIdx != 0) {
            logger.info("Got {} new news", lastIdx);
        }
        for (int i = lastIdx - 1; i >= 0; --i) {
            try {
                bot.sendMessage(creteSendPhotoFromNews(news.get(i)));
                lastNewsDate = news.get(i).getDate();
            } catch (IOException e) {
                logger.error("Error during sending photo", e);
                bot.sendMessage(createMessageFromNews(news.get(i)));
            }
        }
    }

    private SendMessage createMessageFromNews(News news) {
        return new SendMessage()
            .setChatId(channel)
            .disableWebPagePreview()
            .setText(generateTextFromNews(news));
    }

    private SendPhoto creteSendPhotoFromNews(News news) throws IOException {
        return new SendPhoto()
            .setPhoto(news.getLink(), new URL(news.getImageLink()).openStream())
            .setChatId(channel)
            .setCaption(generateTextFromNews(news));
    }

    private String generateTextFromNews(News news) {
        return news.getInfo() + "\n\n" + news.getText() + "\n\n" + news.getLink();
    }
}
