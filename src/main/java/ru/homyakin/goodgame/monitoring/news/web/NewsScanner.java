package ru.homyakin.goodgame.monitoring.news.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.news.web.exceptions.RequestException;

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

    public HttpResponse<String> getLastNews() {
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok: {}", response.statusCode());
                throw new IllegalStateException("Http code is not ok.");
            }
            return response;
        } catch (InterruptedException | IOException e) {
            logger.error("Something went wrong during request", e);
            throw new RequestException("Something went wrong during request", e);
        }
    }
}
