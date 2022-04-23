package ru.homyakin.goodgame.monitoring.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.SavedMessage;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Service
public class ArticleStorage {
    private final static Logger logger = LoggerFactory.getLogger(ArticleStorage.class);
    private final static int TTL_IN_HOURS = 48;
    private final Map<String, SavedMessage> articlesToMessages = new HashMap<>();

    public Optional<SavedMessage> getArticleMessage(Article article) {
        return Optional.ofNullable(articlesToMessages.get(article.link()));
    }

    public void addOrUpdateArticle(Article article, Message message) {
        articlesToMessages.put(
            article.link(),
            new SavedMessage(message, article.toMessageText(), DateTimeUtils.moscowTime())
        );
    }

    public void deleteOldMessages() {
        final var oldArticles = new ArrayList<String>();
        for (final var entry: articlesToMessages.entrySet()) {
            if (Duration.between(
                entry.getValue().lastUpdate(),
                DateTimeUtils.moscowTime()
            ).toHours() > TTL_IN_HOURS) {
                oldArticles.add(entry.getKey());
            }
        }

        for (final var link: oldArticles) {
            articlesToMessages.remove(link);
        }

        if (oldArticles.size() > 0) {
            logger.info("Remove {} messages", oldArticles.size());
        }
    }
}
