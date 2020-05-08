package ru.homyakin.goodgame.monitoring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NewsMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(NewsMonitoring.class);
    private final NewsScanner newsScanner;
    private String lastNewsLink = null;

    public NewsMonitoring(NewsScanner newsScanner) {
        this.newsScanner = newsScanner;
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
        for (int i = lastIdx - 1; i >= 0; --i) {
            //send news
        }
    }
}
