package ru.homyakin.goodgame.monitoring.models;

import jakarta.annotation.Nonnull;

abstract public class EitherError {
    private final String message;

    public EitherError(@Nonnull String message) {
        this.message = message;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }
}
