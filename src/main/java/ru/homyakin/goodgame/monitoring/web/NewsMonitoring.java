package ru.homyakin.goodgame.monitoring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.homyakin.goodgame.monitoring.telegram.Bot;
import ru.homyakin.goodgame.monitoring.web.models.News;

@Service
public class NewsMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(NewsMonitoring.class);
    private final NewsScanner newsScanner;
    private final Bot bot;
    private String lastNewsLink = null;

    public NewsMonitoring(NewsScanner newsScanner, Bot bot) {
        this.newsScanner = newsScanner;
        this.bot = bot;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitorNews() {
        var news = newsScanner.getLastNews();
        if (lastNewsLink == null) {
            logger.info("Initialized monitoring");
            lastNewsLink = news.get(0).getLink();
        }

        int lastIdx = 0;
        while (!news.get(lastIdx).getLink().equals(lastNewsLink) && lastIdx < news.size()) {
            ++lastIdx;
        }
        lastNewsLink = news.get(0).getLink();
        for (int i = lastIdx - 1; i >= 0; --i) {
            bot.sendMessage(createMessageFromNews(news.get(i)));
        }
    }

    private SendMessage createMessageFromNews(News news) {
        var message = new SendMessage();
        var text = news.getInfo() + "\n\n" + news.getText() + "\n\n" + news.getLink();
        return message
            .setChatId("@goodgame_monitoring")
            .disableWebPagePreview()
            .setText(text);
    }
}
