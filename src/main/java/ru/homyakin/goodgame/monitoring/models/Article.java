package ru.homyakin.goodgame.monitoring.models;

import javax.validation.constraints.NotNull;

public record Article(
    @NotNull String mediaLink,
    @NotNull String info,
    @NotNull String text,
    @NotNull String link,
    long date,
    boolean tournament
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
        return info + "\n\n" + text + "\n\n" + link;
    }

    public enum MediaType {
        GIF,
        IMAGE
    }
}
