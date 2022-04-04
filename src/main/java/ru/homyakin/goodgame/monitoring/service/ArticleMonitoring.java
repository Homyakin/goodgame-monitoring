package ru.homyakin.goodgame.monitoring.service;

import java.time.Instant;
import java.util.UUID;
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
    private Long monitoringCount = 0L;

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
        logger.info("initializedDate= " + initializedDate.toString());
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void monitor() {
        final var monitoringUuid = UUID.randomUUID().toString();
        logger.info("Start monitoring " + monitoringUuid);
        final var response = articleScanner.getLastArticles();
        var result = articleParser.parseContent(response.body());
        if (result.isLeft()) {
            userController.notifyAdmin(result.getLeft().getMessage());
        }
        if (monitoringCount % 12 == 0) { // Раз в час
            logger.info("Get new articles: " + result.get().toString());
        }
        result.get().stream()
            .filter(article -> article.date() > initializedDate)
            .forEach(
                article -> storage.getArticleMessage(article)
                    .map(message -> channelController.updateMessage(article, message))
                    .orElseGet(() -> channelController.sendArticle(article))
                    .peek(message -> storage.addOrUpdateArticle(article, message))
                    .peekLeft(error -> {
                        logger.error("Something wrong with {}", article.link());
                        userController.notifyAdmin(error.getMessage());
                    })
            );

        storage.deleteOldMessages();
        ++monitoringCount;
        logger.info("Finish monitoring " + monitoringUuid);
    }
}
