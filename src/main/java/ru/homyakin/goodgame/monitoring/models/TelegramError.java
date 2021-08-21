package ru.homyakin.goodgame.monitoring.models;

import javax.validation.constraints.NotNull;

public class TelegramError extends EitherError {
    public TelegramError(@NotNull String message) {
        super(message);
    }
}
