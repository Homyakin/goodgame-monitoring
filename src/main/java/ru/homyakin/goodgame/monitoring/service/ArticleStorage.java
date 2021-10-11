package ru.homyakin.goodgame.monitoring.service;

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
public class ArticleStorage {
    private final Map<String, Message> lastArticles = new HashMap<>();

    public Optional<Message> getArticleMessage(String url) {
        return Optional.ofNullable(lastArticles.get(url));
    }

    public void insertArticle(String url, Message message) {
        lastArticles.put(url, message);
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void removeOldArticles() {
        var oldArticles = new ArrayList<String>();
        for (var article : lastArticles.entrySet()) {
            int ttl = 48;
            if (Optional.ofNullable(article.getValue().getCaption()).orElse("").contains("cup")) {
                ttl = 48; // TODO нужно поменять логику. Если новость еще в находится на странице, ее нельзя удалять.
            }
            if (
                Duration.between(
                    DateTimeUtils.longToMoscowDateTime(article.getValue().getDate()),
                    DateTimeUtils.moscowTime()
                ).toHours() > ttl
            ) {
                oldArticles.add(article.getKey());
            }
        }
        for (var url : oldArticles) {
            lastArticles.remove(url);
        }
    }
}
