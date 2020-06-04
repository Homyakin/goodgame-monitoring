package ru.homyakin.goodgame.monitoring.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.web.exceptions.RequestException;
import ru.homyakin.goodgame.monitoring.web.models.News;

@Component
public class NewsScanner {
    private final static Logger logger = LoggerFactory.getLogger(NewsScanner.class);
    private final HttpClient client;
    private final HttpRequest request;
    private final DateTimeFormatter formatter;

    public NewsScanner() {
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
            .uri(URI.create("https://goodgame.ru/news/"))
            .GET()
            .build();
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    public List<News> getLastNews() {
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok: {}", response.statusCode());
                throw new IllegalStateException("Http code is not ok.");
            }
            return parseContent(response.body());
        } catch (InterruptedException | IOException e) {
            logger.error("Something went wrong during request", e);
            throw new RequestException("Something went wrong during request", e);
        }
    }

    private List<News> parseContent(String html) {
        var doc = Jsoup.parse(html);
        var elements = doc.getElementsByClass("news-element");
        List<News> news = new ArrayList<>();
        for (var element : elements) {
            news.add(createNews(element));
        }
        news.sort((n1, n2) -> n2.getDate().compareTo(n1.getDate()));
        return news;
    }

    private News createNews(Element newsElement) {
        var imageLink = getImageLink(newsElement);
        var infoBlock = getInfoBlock(newsElement);
        var info = getInfo(infoBlock);
        var text = getText(infoBlock);
        var link = getLink(infoBlock);
        var date = getDate(infoBlock);
        return new News(imageLink, info, text, link, date);
    }

    private Element getInfoBlock(Element newsElement) {
        return newsElement
            .getElementsByClass("info-block")
            .get(0);
    }

    private String getImageLink(Element newsElement) {
        var link = newsElement
            .getElementsByClass("img-block")
            .get(0)
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("gg-webp");
        if (link.equals("")) { //tournaments
            link = newsElement
                .getElementsByClass("img-block")
                .get(0)
                .getElementsByTag("a")
                .get(0)
                .attributes()
                .get("style");
            link = link.substring(23, link.length() - 2);
        } else {
            //jpg and png are usually cropped and there are no webp file by get method
            link = link.replace(".jpg", ".webp");
            link = link.replace(".png", ".webp");
            link = "https://static.goodgame.ru" + link;
        }
        return link;
    }

    private String getInfo(Element infoElement) {
        return infoElement
            .getElementsByTag("h3")
            .get(0)
            .text();
    }

    private String getText(Element infoElement) {
        var text = "";
        var textBlocks = infoElement.getElementsByClass("text-block");
        if (textBlocks.size() != 0) {
            var textTag = textBlocks
                .get(0)
                .getElementsByTag("p");
            if (textTag.size() != 0) {
                text = textTag
                    .get(0)
                    .text();
            }
            var listBlock = textBlocks
                .get(0)
                .getElementsByTag("ul");
            if (listBlock.size() != 0) {
                text += "\n\n" + getList(listBlock.get(0));
            }
        } else {
            text = getTournamentInfo(infoElement);
        }
        return text;
    }

    private String getList(Element listElement) {
        var items = listElement.getElementsByTag("li");
        var builder = new StringBuilder("");
        for (var item: items) {
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

    private String getTournamentInfo(Element infoElement) {
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
                if (names.get(i).getElementsByTag("gg-local-time").size() != 0) {
                    var timestamp = Long.valueOf(
                        names
                            .get(i)
                            .getElementsByTag("gg-local-time")
                            .get(0)
                            .attributes()
                            .get("utc-timestamp")
                    );
                    var dateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
                    tournamentText.append(labels.get(i).text()).append(": ").append(dateTime.format(formatter)).append("\n");
                } else {
                    tournamentText.append(labels.get(i).text()).append(": ").append(names.get(i).text()).append("\n");
                }
            }
        }
        return builder.append(updateText).append(tournamentText).toString();
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
