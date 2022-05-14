package ru.homyakin.goodgame.monitoring.models;

import javax.validation.constraints.NotNull;

public record Article(
    @NotNull String mediaLink,
    @NotNull String title,
    @NotNull String text,
    @NotNull String link,
    long id,
    long date,
    boolean isTournament
) {

    public MediaType getMediaType() {
        if (mediaLink.endsWith(".gif")) {
            return MediaType.GIF;
        } else {
            return MediaType.IMAGE;
        }
    }

    @Override
    public String toString() {
        return String.format("Article(link=%s, date=%d)", link, date);
    }

    public String toMessageText() {
        var s = title + "\n\n" + text;
        if (s.endsWith("\n")) {
            s += "\n";
        } else {
            s += "\n\n";
        }
        return s + link;
    }

    public enum MediaType {
        GIF,
        IMAGE
    }
}
