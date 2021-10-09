package ru.homyakin.goodgame.monitoring.service.parser;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.models.Article;

@Component
public class ArticleParser {
    private final ImageLinkParser imageLinkParser;
    private final TournamentParser tournamentParser;

    public ArticleParser(ImageLinkParser imageLinkParser, TournamentParser tournamentParser) {
        this.imageLinkParser = imageLinkParser;
        this.tournamentParser = tournamentParser;
    }

    public List<Article> parseContent(String html) {
        var doc = Jsoup.parse(html);
        var elements = doc.getElementsByClass("news-element");
        List<Article> articles = new ArrayList<>();
        for (var element : elements) {
            articles.add(createArticle(element));
        }
        articles.sort((n1, n2) -> Long.compare(n2.date(), n1.date()));
        return articles;
    }

    private Article createArticle(Element articleElement) {
        var imageLink = imageLinkParser.getImageLink(articleElement);
        var infoBlock = getInfoBlock(articleElement);
        var tournament = isTournament(infoBlock);
        var info = getInfo(infoBlock);
        String text;
        if (tournament) {
            text = tournamentParser.getTournamentInfo(infoBlock);
        } else {
            text = getText(infoBlock);
        }
        var link = getLink(infoBlock);
        var date = getDate(infoBlock);
        return new Article(imageLink, info, text, link, date, tournament);
    }

    private Element getInfoBlock(Element articleElement) {
        return articleElement
            .getElementsByClass("info-block")
            .get(0);
    }

    private String getInfo(Element infoElement) {
        return infoElement
            .getElementsByTag("h3")
            .get(0)
            .text();
    }

    private boolean isTournament(Element infoElement) {
        return infoElement.getElementsByClass("text-block").size() == 0;
    }

    private String getText(Element infoElement) {
        var text = "";
        var textBlocks = infoElement.getElementsByClass("text-block");
        text += getUpdate(infoElement);
        var textTags = textBlocks
            .get(0)
            .getElementsByTag("p");
        var textBuilder = new StringBuilder();
        int size = textTags.size();
        for (int i = 0; i < size; ++i) {
            textBuilder.append(textTags.get(i).text());
            if (i != size - 1) {
                textBuilder.append("\n");
            }
        }
        text += textBuilder.toString();
        var listBlock = textBlocks
            .get(0)
            .getElementsByTag("ul");
        if (listBlock.size() != 0) {
            text += "\n" + getList(listBlock.get(0));
        }
        return text;
    }

    private String getUpdate(Element infoElement) {
        var blocks = infoElement.getElementsByClass("update-block");
        if (blocks.size() != 0) {
            return "Обновление: " + blocks.get(0).text() + "\n\n";
        } else {
            return "";
        }
    }

    private String getList(Element listElement) {
        var items = listElement.getElementsByTag("li");
        var builder = new StringBuilder("");
        for (var item : items) {
            builder.append("• ").append(item.text()).append("\n");
        }
        return builder.toString();
    }

    private String getLink(Element infoElement) {
        return infoElement
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("href");
    }

    private Long getDate(Element infoElement) {
        return Long.valueOf(
            infoElement
                .getElementsByClass("date")
                .get(0)
                .getElementsByTag("gg-local-time")
                .get(0)
                .attributes()
                .get("utc-timestamp")
        );
    }
}