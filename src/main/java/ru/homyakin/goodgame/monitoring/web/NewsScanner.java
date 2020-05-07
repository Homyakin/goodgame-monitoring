package ru.homyakin.goodgame.monitoring.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
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

    public NewsScanner() {
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
            .uri(URI.create("https://goodgame.ru/news/"))
            .GET()
            .build();
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

    private News createNews(Element element) {
        var imageBlock = element.getElementsByClass("img-block").get(0);
        var infoBlock = element.getElementsByClass("info-block").get(0);
        var info = infoBlock
            .getElementsByTag("h3")
            .get(0)
            .text();
        var text = "";
        var textBlocks = infoBlock.getElementsByClass("text-block");
        if (textBlocks.size() != 0) { //TODO For example tournaments don't have a text field https://goodgame.ru/cup/9398/
            text = textBlocks
                .get(0)
                .getElementsByTag("p")
                .get(0)
                .text();
        }
        var link = infoBlock
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("href");
        var imageLink = "https://static.goodgame.ru" + imageBlock
            .getElementsByTag("a")
            .get(0)
            .attributes()
            .get("gg-webp");
        var date = Long.valueOf(
            infoBlock
            .getElementsByClass("date")
            .get(0)
            .getElementsByTag("gg-local-time")
            .get(0)
            .attributes()
            .get("utc-timestamp")
        );
        return new News(imageLink, info, text, link, date);
    }
}
