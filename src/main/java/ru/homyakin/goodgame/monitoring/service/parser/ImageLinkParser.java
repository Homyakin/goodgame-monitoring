package ru.homyakin.goodgame.monitoring.service.parser;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class ImageLinkParser {
    public String getImageLink(Element articleElement) {
        var link = articleElement
            .getElementsByClass("img-block")
            .get(0)
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("gg-webp");
        if (link.equals("")) {
            link = getTournamentImageLink(articleElement);
        } else {
            // jpg и png обычно обрезаны, поэтому нужно использовать одноименный .webp файл
            link = link.replace(".jpg", ".webp");
            link = link.replace(".png", ".webp");
            link = "https://static.goodgame.ru" + link;
        }
        return link;
    }

    private String getTournamentImageLink(Element articleElement) {
        // В обычных новостях здесь лежит заглушка
        final var link = articleElement
            .getElementsByClass("img-block")
            .get(0)
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("style");
        return link.substring(23, link.length() - 2);
    }
}
