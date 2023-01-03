package ru.homyakin.goodgame.monitoring.models;

public record ArticleInfo(
    String title,
    String link,
    Long comments,
    long date,
    long views
) {
    public long calculatePopularity(long initDate) {
        final long passedHours = Math.min((date - initDate) / 3600, 336);
        return passedHours * 30 + comments * 50 + views;
    }

    public String toTelegramText() {
        return String.format(
            """
            <a href="%s">%s</a> (Просмотры: %d; комментарии: %d)
            """,
            link,
            title,
            views,
            comments
        );
    }
}
