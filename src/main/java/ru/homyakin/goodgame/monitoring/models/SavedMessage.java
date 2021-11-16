package ru.homyakin.goodgame.monitoring.models;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;

public record SavedMessage(
    @NotNull Message message,
    @NotNull LocalDateTime lastUpdate
) {
}
