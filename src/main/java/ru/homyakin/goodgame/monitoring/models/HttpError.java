package ru.homyakin.goodgame.monitoring.models;

public class HttpError extends EitherError {
    public HttpError(String message) {
        super(message);
    }
}
