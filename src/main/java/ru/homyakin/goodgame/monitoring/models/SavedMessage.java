package ru.homyakin.goodgame.monitoring.models;

import java.time.LocalDateTime;

import jakarta.annotation.Nonnull;
import org.telegram.telegrambots.meta.api.objects.Message;

public record SavedMessage(
    @Nonnull Message message,
    @Nonnull String sentText, // Нужен, чтобы понимать редактировать сообщение или нет. Из-за использования HTML parse mode.
    @Nonnull LocalDateTime lastUpdate
) {
}
