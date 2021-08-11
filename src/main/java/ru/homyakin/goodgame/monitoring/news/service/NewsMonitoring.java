package ru.homyakin.goodgame.monitoring.news.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.news.service.parser.NewsParser;
import ru.homyakin.goodgame.monitoring.news.web.NewsScanner;
import ru.homyakin.goodgame.monitoring.telegram.ChannelController;

@Service
public class NewsMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(NewsMonitoring.class);
    private final NewsScanner newsScanner;
    private final NewsStorage storage;
    private final NewsParser newsParser;
    private final ChannelController channelController;
    private Long lastNewsDate = null;

    public NewsMonitoring(
        NewsScanner newsScanner,
        NewsStorage storage,
        NewsParser newsParser,
        ChannelController channelController
    ) {
        this.newsScanner = newsScanner;
        this.storage = storage;
        this.newsParser = newsParser;
        this.channelController = channelController;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitorNews() {
        var response = newsScanner.getLastNews();
        var news = newsParser.parseContent(response.body());
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
            var message = channelController.sendNews(
                news.get(i),
                storage.getNews(news.get(i).getLink()).orElse(null)
            );
            if (message.isPresent() && !news.get(i).isTournament()) {
                storage.insertNews(news.get(i).getLink(), message.get());
            }
            if (message.isEmpty()) {
                // TODO отправить сообщение админу
                logger.error("Something wrong with {}", news.get(i).getLink());
            }
            lastNewsDate = news.get(i).getDate();
        }
    }

}
