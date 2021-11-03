package ru.homyakin.goodgame.monitoring.service;

import java.time.Instant;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleParser;
import ru.homyakin.goodgame.monitoring.telegram.ChannelController;
import ru.homyakin.goodgame.monitoring.telegram.UserController;

@Service
public class ArticleMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(ArticleMonitoring.class);
    private final ArticleScanner articleScanner;
    private final ArticleStorage storage;
    private final ArticleParser articleParser;
    private final ChannelController channelController;
    private final UserController userController;
    private final Long initializedDate = Instant.now().getEpochSecond();

    public ArticleMonitoring(
        ArticleScanner articleScanner,
        ArticleStorage storage,
        ArticleParser articleParser,
        ChannelController channelController,
        UserController userController
    ) {
        this.articleScanner = articleScanner;
        this.storage = storage;
        this.articleParser = articleParser;
        this.channelController = channelController;
        this.userController = userController;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitor() {
        final var response = articleScanner.getLastArticles();
        final var articles = articleParser.parseContent(response.body()).stream()
            .filter(article -> article.date() > initializedDate)
            .toList();
        articles.forEach(article -> {
                final var result = storage.getArticleMessage(article)
                    .map(message -> channelController.updateMessage(article, message))
                    .orElseGet(() -> channelController.sendArticle(article));
                result
                    .peek(message -> storage.insertArticle(article, message))
                    .peekLeft(error -> {
                        logger.error("Something wrong with {}", article.link());
                        userController.notifyAdmin(error.getMessage());
                    });
            });
        storage.markArticlesNotOnNewsPage(articles);
    }
}
