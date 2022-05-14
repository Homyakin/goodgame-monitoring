package ru.homyakin.goodgame.monitoring.models;

public record ArticleInfo(
    String title,
    String link,
    String comments,
    long date,
    long views
) {
    public String toTelegramText() {
        return String.format(
            """
            <a href="%s">%s</a> (Просмотры: %d; %s)
            """,
            link,
            title,
            views,
            comments
        );
    }
}
