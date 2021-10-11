package ru.homyakin.goodgame.monitoring.models;

import javax.validation.constraints.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;

public record SavedArticle(
    @NotNull Article article,
    @NotNull Message message,
    boolean isOnNewsPage
) {
    public SavedArticle copyWithNotOnNewsPage() {
        return new SavedArticle(
            article,
            message,
            false
        );
    }
}
