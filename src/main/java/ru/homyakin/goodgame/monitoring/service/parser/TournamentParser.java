package ru.homyakin.goodgame.monitoring.service.parser;

import java.time.format.DateTimeFormatter;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Component
public class TournamentParser {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public String getTournamentInfo(Element infoElement) {
        var builder = new StringBuilder("");
        var update = infoElement.getElementsByClass("update-block");
        var updateText = "";
        if (update.size() != 0) {
            updateText = update
                .get(0)
                .getElementsByTag("p")
                .get(0)
                .text() + "\n\n";
        }
        var tournament = infoElement.getElementsByClass("tournaments-wrap");
        var tournamentText = new StringBuilder("");
        if (tournament.size() != 0) {
            var labels = tournament.get(0).getElementsByClass("label");
            var names = tournament.get(0).getElementsByClass("name");
            int size = labels.size();
            for (int i = 0; i < size; ++i) {
                tournamentText.append(parseBlock(names.get(i), labels.get(i)));
            }
        }
        return builder.append(updateText).append(tournamentText).toString();
    }

    private String parseBlock(Element name, Element label) {
        String nameText;
        if (name.getElementsByTag("gg-local-time").size() != 0) {
            var timestamp = Long.parseLong(
                name
                    .getElementsByTag("gg-local-time")
                    .get(0)
                    .attributes()
                    .get("utc-timestamp")
            );
            var dateTime = DateTimeUtils.longToMoscowDateTime(timestamp);
            nameText = dateTime.format(formatter);
        } else {
            nameText = name.text();
        }
        return String.format("%s: %s\n", label.text(), nameText);
    }
}
