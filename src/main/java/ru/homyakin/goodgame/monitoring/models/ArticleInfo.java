package ru.homyakin.goodgame.monitoring.models;

public record ArticleInfo(
    String title,
    String link,
    String comments,
    long date,
    long views
) {
    public long calculatePopularity(long initDate) {
        final long commentsCount = Long.parseLong(comments.split(" ")[0]);
        final long passedHours = (date - initDate) / 3600;
        return passedHours * 30 + commentsCount * 50 + views;
    }

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
