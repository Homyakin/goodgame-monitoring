package ru.homyakin.goodgame.monitoring.article.models;

public record Article(
    String imageLink,
    String info,
    String text,
    String link,
    Long date,
    boolean tournament
) {
    @Override
    public String toString() {
        return info + "\n\n" + text + "\n\n" + link;
    }
}
