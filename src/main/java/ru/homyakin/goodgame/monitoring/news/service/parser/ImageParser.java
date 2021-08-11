package ru.homyakin.goodgame.monitoring.news.service.parser;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class ImageParser {
    public String getImageLink(Element newsElement) {
        var link = newsElement
            .getElementsByClass("img-block")
            .get(0)
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("gg-webp");
        if (link.equals("")) {
            link = getTournamentImageLink(newsElement);
        } else {
            // jpg и png обычно обрезаны, поэтому нужно использовать одноименный .webp файл
            link = link.replace(".jpg", ".webp");
            link = link.replace(".png", ".webp");
            link = "https://static.goodgame.ru" + link;
        }
        return link;
    }

    private String getTournamentImageLink(Element newsElement) {
        // В обычных новостях здесь лежит заглушка
        var link = newsElement
            .getElementsByClass("img-block")
            .get(0)
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("style");
        return link.substring(23, link.length() - 2);
    }
}
