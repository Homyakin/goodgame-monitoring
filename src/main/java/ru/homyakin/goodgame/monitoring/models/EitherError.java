package ru.homyakin.goodgame.monitoring.models;

abstract public class EitherError {
    private final String message;

    public EitherError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
