package ru.homyakin.goodgame.monitoring.service;

import io.vavr.control.Either;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.HttpError;
import ru.homyakin.goodgame.monitoring.models.ParserError;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleInfoParser;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleParser;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;

@Component
public class GoodGameScanner {
    private final static Logger logger = LoggerFactory.getLogger(GoodGameScanner.class);
    private final HttpClient client;
    private final HttpRequest newsRequest;

    public GoodGameScanner() {
        client = HttpClient.newHttpClient();
        newsRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://goodgame.ru/news/"))
            .GET()
            .build();
    }

    public Either<EitherError, Article> getLastArticle() {
        return getLastArticles()
            .map(articles -> articles
                .stream()
                .filter(article -> !article.isTournament())
                .max(Comparator.comparingLong(Article::date))
            ).flatMap(
                article -> article
                    .map(Either::<EitherError, Article>right)
                    .orElseGet(() -> Either.left(new ParserError("Can't find last article")))
            );
    }

    public Either<EitherError, List<Article>> getLastArticles() {
        return sendRequest(newsRequest)
            .flatMap(response -> ArticleParser.parseContent(response.body()));
    }

    public Either<EitherError, ArticleInfo> getArticleInfoById(String id) {
        final var link = "https://goodgame.ru/news/" + id;
        logger.info("Getting page " + link);
        final var articleInfoRequest = HttpRequest.newBuilder()
            .uri(URI.create(link))
            .GET()
            .build();
        return sendRequest(articleInfoRequest)
            .flatMap(response -> ArticleInfoParser.parseBody(response.body(), link));
    }

    private Either<EitherError, HttpResponse<String>> sendRequest(HttpRequest request) {
        try {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                logger.error("Http code is not ok: {}", response.statusCode());
                return Either.left(new HttpError("Http code is not ok: " + response.statusCode()));
            }
            return Either.right(response);
        } catch (InterruptedException | IOException e) {
            logger.error("Something went wrong during request", e);
            return Either.left(new HttpError("Something went wrong during request " + CommonUtils.getStringStackTrace(e)));
        }
    }
}
