package ru.homyakin.goodgame.monitoring.news.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Service
public class NewsStorage {
    private final Map<String, Message> lastNews = new HashMap<>();

    public Optional<Message> getNews(String url) {
        return Optional.ofNullable(lastNews.get(url));
    }

    public void insertNews(String url, Message message) {
        lastNews.put(url, message);
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void checkOldNews() {
        var oldNews = new ArrayList<String>();
        for (var news : lastNews.entrySet()) {
            if (
                Duration.between(
                    DateTimeUtils.longToMoscowDateTime(news.getValue().getDate()),
                    DateTimeUtils.moscowTime()
                ).toHours() > 48
            ) {
                oldNews.add(news.getKey());
            }
        }
        for (var url : oldNews) {
            lastNews.remove(url);
        }
    }
}
