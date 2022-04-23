package ru.homyakin.goodgame.monitoring.models;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;

public record SavedMessage(
    @NotNull Message message,
    @NotNull String sentText, // Нужен, чтобы понимать редактировать сообщение или нет. Из-за использования HTML parse mode.
    @NotNull LocalDateTime lastUpdate
) {
}
