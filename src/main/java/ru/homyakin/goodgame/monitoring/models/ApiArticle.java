package ru.homyakin.goodgame.monitoring.models;

public record ApiArticle(
    Long id,
    String title,
    String key,
    Long date,
    Long views
) {
    public ArticleInfo toArticleInfo(ApiComments apiComments) {
        return new ArticleInfo(
            title,
            "https://goodgame.ru/news/" + key,
            apiComments.info().count(),
            date,
            views
        );
    }
}
