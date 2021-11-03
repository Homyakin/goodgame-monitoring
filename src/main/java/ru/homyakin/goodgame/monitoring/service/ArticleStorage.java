package ru.homyakin.goodgame.monitoring.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.config.StorageConfiguration;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.SavedArticle;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Service
public class ArticleStorage {
    private final Map<String, SavedArticle> lastArticles = new HashMap<>();
    private Integer newsTtlInHours;
    private Integer tournamentTtlInHours;

    public ArticleStorage(StorageConfiguration configuration) {
        newsTtlInHours = configuration.getNewsTtlInHours();
        tournamentTtlInHours = configuration.getTournamentTtlInHours();
    }

    public Optional<Message> getArticleMessage(Article article) {
        return Optional.ofNullable(lastArticles.get(article.link())).map(SavedArticle::message);
    }

    public void insertArticle(Article article, Message message) {
        lastArticles.put(article.link(), new SavedArticle(article, message, true));
    }

    public void markArticlesNotOnNewsPage(List<Article> articles) {
        final var notOnNewsPage = new ArrayList<String>();
        for (final var savedArticle: lastArticles.entrySet()) {
            if (!articles.contains(savedArticle.getValue().article())) {
                notOnNewsPage.add(savedArticle.getKey());
            }
        }
        for (final var url: notOnNewsPage) {
            final var savedArticle = lastArticles.get(url);
            if (savedArticle.isOnNewsPage()) {
                lastArticles.put(url, savedArticle.copyWithNotOnNewsPage());
            }
        }
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void removeOldArticles() {
        final var oldArticles = new ArrayList<String>();
        for (final var article : lastArticles.entrySet()) {
            if (article.getValue().isOnNewsPage()) continue;
            int ttl = newsTtlInHours;
            if (article.getValue().article().isTournament()) {
                ttl = tournamentTtlInHours;
            }
            if (
                Duration.between(
                    DateTimeUtils.longToMoscowDateTime(article.getValue().message().getDate()),
                    DateTimeUtils.moscowTime()
                ).toHours() > ttl
            ) {
                oldArticles.add(article.getKey());
            }
        }
        for (final var url : oldArticles) {
            lastArticles.remove(url);
        }
    }
}
