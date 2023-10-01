package ru.homyakin.goodgame.monitoring.models;

import jakarta.annotation.Nonnull;

public record Article(
    @Nonnull String mediaLink,
    @Nonnull String title,
    @Nonnull String text,
    @Nonnull String link,
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

    public String toMessageText(int maxLength) {
        var s = toMessageText();
        if (s.length() > maxLength) {
            s = title + "\n\n" + text.substring(0, text.length() - (s.length() - maxLength + 5)) + "...";
            s += "\n\n" + link;
        }
        return s;
    }

    public enum MediaType {
        GIF,
        IMAGE
    }
}
