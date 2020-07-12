package ru.homyakin.goodgame.monitoring.web.models;

public class News {
    private final String link;
    private final String imageLink;
    private final String info;
    private final String text;
    private final Long date;


    public News(String imageLink, String info, String text, String link, Long date) {
        this.imageLink = imageLink;
        this.info = info;
        this.text = text;
        this.link = link;
        this.date = date;
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

    @Override
    public String toString() {
        return info + "\n\n" + text + "\n\n" + link;
    }
}
