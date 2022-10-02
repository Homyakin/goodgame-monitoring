package ru.homyakin.goodgame.monitoring.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.annotation.Nullable;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

public record GoodGameArticle(
    String id, // для турниров -> https://goodgame.ru/cup/<id>
    int status,
    @Nullable String key, // только для новостей -> https://goodgame.ru/news/<key>
    String logo,
    @Nullable String title, // только для турниров
    @Nullable Game game, // только для турниров и то не всегда
    @Nullable Long start, // только для турниров
    @JsonProperty("prize_fund")
    @Nullable String prizeFund, // только для турниров
    @Nullable String participants, // только для турниров
    @Nullable String update, // только для турниров
    @Nullable Description description, // только для новостей
    long date
) {
    public Article toArticle() {
        return new Article(
            getMediaLink(),
            getTitle(),
            getText(),
            getLink(),
            key != null ? Long.parseLong(key) : Long.parseLong(id),
            date,
            isTournament()
        );
    }

    private String getLink() {
        if (key == null) {
            return "https://goodgame.ru/cup/" + id;
        } else {
            return "https://goodgame.ru/news/" + key;
        }
    }

    private String getTitle() {
        if (description == null) {
            return title;
        } else {
            return description.title();
        }
    }

    private String getText() {
        if (description != null) {
            return description.getText();
        } else {
            return getTournamentText();
        }
    }

    private String getTournamentText() {
        Objects.requireNonNull(start);
        Objects.requireNonNull(prizeFund);
        Objects.requireNonNull(participants);

        var text = "";
        if (update != null) {
            var prettyUpdate = CommonUtils.removeHtmlElements(update)
                .replace("\n\n", "\n");
            text = prettyUpdate;
            if (prettyUpdate.endsWith("\n")) {
                text += "\n";
            } else {
                text += "\n\n";
            }
        }
        if (game != null) {
            text += "Игра: " + game.title() + "\n";
        }
        text += "Призовой фонд: " + prizeFund + "\n";
        text += "Участники: " + participants + "\n";
        text += "Начало турнира: " + DateTimeUtils.longToTimeString(start);

        return text;
    }

    private String getMediaLink() {
        if (logo.startsWith("http")) {
            return logo;
        } else {
            return "https://goodgame.ru" + logo;
        }
    }

    private boolean isTournament() {
        return key == null;
    }
}

record Description(
    String title,
    @JsonProperty("short_description")
    String shortDescription,
    String update,
    @JsonProperty("update_text")
    String updateText
) {
    public String getText() {
        var text = "";
        if (!update.isBlank()) {
            text = "Обновление: " + update + "\n\n";
        }
        text += shortDescription;
        return CommonUtils.removeHtmlElements(text);
    }
}

record Game(
    String title
) {}
