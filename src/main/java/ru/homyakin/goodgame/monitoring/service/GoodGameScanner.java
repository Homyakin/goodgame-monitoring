package ru.homyakin.goodgame.monitoring.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoodGameScanner {
    private final static Logger logger = LoggerFactory.getLogger(GoodGameScanner.class);
    private final HttpClient client;
    private final HttpRequest request;

    public GoodGameScanner() {
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
            .uri(URI.create("https://goodgame.ru/news/"))
            .GET()
            .build();
    }

    public HttpResponse<String> getLastArticles() {
        try {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok: {}", response.statusCode());
                throw new IllegalStateException("Http code is not ok.");
            }
            return response;
        } catch (InterruptedException | IOException e) {
            logger.error("Something went wrong during request", e);
            throw new IllegalStateException("Something went wrong during request", e);
        }
    }

    public Optional<HttpResponse<String>> getPageByLink(String link) {
        try {
            logger.info("Getting page " + link);
            final var articleInfoRequest = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .GET()
                .build();
            final var response = client.send(articleInfoRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok at link {}: {}", link, response.statusCode());
                return Optional.empty();
            }
            return Optional.of(response);
        } catch (InterruptedException | IOException e) {
            logger.error("Something went wrong during request", e);
            return Optional.empty();
        }
    }
}
