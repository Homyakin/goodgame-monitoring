package ru.homyakin.goodgame.monitoring.article.web.exceptions;

public class RequestException extends RuntimeException {

    public RequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
