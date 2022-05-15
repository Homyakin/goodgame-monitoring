package ru.homyakin.goodgame.monitoring.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.telegram.ChannelController;
import ru.homyakin.goodgame.monitoring.telegram.UserController;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Service
public class ArticleMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(ArticleMonitoring.class);
    private final GoodGameScanner scanner;
    private final ArticleStorage storage;
    private final ChannelController channelController;
    private final UserController userController;
    private final Long initializedDate = Instant.now().getEpochSecond();

    public ArticleMonitoring(
        GoodGameScanner scanner,
        ArticleStorage storage,
        ChannelController channelController,
        UserController userController
    ) {
        this.scanner = scanner;
        this.storage = storage;
        this.channelController = channelController;
        this.userController = userController;
        logger.info("initializedDate = " + initializedDate.toString());
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void monitor() {
        final var monitoringUuid = UUID.randomUUID().toString();
        logger.info("Start monitoring " + monitoringUuid);
        final var response = scanner.getLastArticles();
        if (response.isLeft()) {
            userController.notifyAdmin(response.getLeft().getMessage());
            return;
        }
        response.get().stream()
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
        logger.info("Finish monitoring " + monitoringUuid);
    }

    @Scheduled(cron = "0 0 12 * * 7", zone = "Europe/Moscow")
    public void findTopWeekArticles() {
        // Хотим топ новостей с прошлой субботы по текущую пятницу
        logger.info("Start searching top week articles");
        final var startDate = DateTimeUtils.getSaturdayAtPreviousWeekTime();
        final var endDate = DateTimeUtils.getSaturdayAtThisWeekTime();

        final var result = scanner.getLastArticle();
        if (result.isLeft()) {
            userController.notifyAdmin(result.getLeft().getMessage());
            return;
        }
        long id = result.get().id();
        ArticleInfo articleInfo = null;
        final var weekArticles = new ArrayList<ArticleInfo>();
        while (articleInfo == null || articleInfo.date() >= startDate) {
            var response = scanner.getArticleInfoById(String.valueOf(id));
            --id;
            if (response.isLeft()) {
                continue;
            }
            articleInfo = response.get();
            if (articleInfo.date() < endDate && articleInfo.date() >= startDate) {
                weekArticles.add(articleInfo);
            }
        }
        channelController.sendArticles(
            weekArticles.stream().sorted((a, b) -> Long.compare(b.views(), a.views())).limit(5).toList(),
            startDate,
            endDate
        );
    }
}
