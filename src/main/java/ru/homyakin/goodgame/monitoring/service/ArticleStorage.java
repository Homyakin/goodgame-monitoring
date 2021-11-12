package ru.homyakin.goodgame.monitoring.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.SavedArticle;

@Service
public class ArticleStorage {
    private final Map<String, SavedArticle> articlesOnNewsPage = new HashMap<>();

    public Optional<Message> getArticleMessage(Article article) {
        return Optional.ofNullable(articlesOnNewsPage.get(article.link())).map(SavedArticle::message);
    }

    public void refreshArticlesOnNewsPage(List<SavedArticle> savedArticles) {
        articlesOnNewsPage.clear();
        for (final var savedArticle: savedArticles) {
            articlesOnNewsPage.put(savedArticle.article().link(), savedArticle);
        }
    }
}
