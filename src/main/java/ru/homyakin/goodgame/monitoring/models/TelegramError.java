package ru.homyakin.goodgame.monitoring.models;

import jakarta.annotation.Nonnull;

public class TelegramError extends EitherError {
    public TelegramError(@Nonnull String message) {
        super(message);
    }
}
