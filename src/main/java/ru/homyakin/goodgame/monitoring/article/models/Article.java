package ru.homyakin.goodgame.monitoring.article.models;

public class Article {
    private final String link;
    private final String imageLink;
    private final String info;
    private final String text;
    private final Long date;
    private final boolean tournament;


    public Article(String imageLink, String info, String text, String link, Long date, boolean tournament) {
        this.imageLink = imageLink;
        this.info = info;
        this.text = text;
        this.link = link;
        this.date = date;
        this.tournament = tournament;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getLink() {
        return link;
    }

    public Long getDate() {
        return date;
    }

    public boolean isTournament() {
        return tournament;
    }

    @Override
    public String toString() {
        return info + "\n\n" + text + "\n\n" + link;
    }
}
