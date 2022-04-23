package ru.homyakin.goodgame.monitoring.models;

public class TelegramEditingError extends TelegramError {
    public final static String TELEGRAM_ERROR_MESSAGE = "[400] Bad Request";

    public TelegramEditingError(String message) {
        super(message);
    }
}
