package ru.homyakin.goodgame.monitoring.models;

import javax.validation.constraints.NotNull;

abstract public class EitherError {
    private final String message;

    public EitherError(@NotNull String message) {
        this.message = message;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
}
