package ru.homyakin.goodgame.monitoring.models;

public class TelegramError extends EitherError {
    public TelegramError(String message) {
        super(message);
    }
}
