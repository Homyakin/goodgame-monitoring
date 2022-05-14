package ru.homyakin.goodgame.monitoring.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleInfoParser;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleParser;
import ru.homyakin.goodgame.monitoring.telegram.ChannelController;
import ru.homyakin.goodgame.monitoring.telegram.UserController;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

@Service
public class ArticleMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(ArticleMonitoring.class);
    private final GoodGameScanner scanner;
    private final ArticleStorage storage;
    private final ArticleParser articleParser;
    private final ChannelController channelController;
    private final UserController userController;
    private final Long initializedDate = Instant.now().getEpochSecond();

    public ArticleMonitoring(
        GoodGameScanner scanner,
        ArticleStorage storage,
        ArticleParser articleParser,
        ChannelController channelController,
        UserController userController
    ) {
        this.scanner = scanner;
        this.storage = storage;
        this.articleParser = articleParser;
        this.channelController = channelController;
        this.userController = userController;
        logger.info("initializedDate = " + initializedDate.toString());
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void monitor() {
        final var monitoringUuid = UUID.randomUUID().toString();
        logger.info("Start monitoring " + monitoringUuid);
        final var response = scanner.getLastArticles();
        var result = articleParser.parseContent(response.body());
        if (result.isLeft()) {
            userController.notifyAdmin(result.getLeft().getMessage());
            return;
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
        logger.info("Finish monitoring " + monitoringUuid);
    }

    @Scheduled(cron = "0 0 12 * * 6", zone = "Europe/Moscow")
    public void findTopWeekArticles() {
        // Хотим топ новостей с прошлой субботы по текущую пятницу
        final var startDate = DateTimeUtils.getPreviousSaturdayTime();
        final var endDate = DateTimeUtils.getCloseSaturdayTime();

        final var result = getLastArticle();
        if (result.isEmpty()) {
            return;
        }
        long id = result.get().id();
        ArticleInfo articleInfo = null;
        final var weekArticles = new ArrayList<ArticleInfo>();
        do {
            final var link = "https://goodgame.ru/news/" + id;
            var response = scanner.getPageByLink(link);
            --id;
            if (response.isEmpty()) {
                continue;
            }
            var parseResult = ArticleInfoParser.parseBody(response.get().body(), link);
            if (parseResult.isEmpty()) {
                continue;
            }
            articleInfo = parseResult.get();
            if (articleInfo.date() < endDate && articleInfo.date() > startDate) {
                weekArticles.add(articleInfo);
            }
        } while (articleInfo == null || articleInfo.date() > startDate);
        channelController.sendArticles(
            weekArticles.stream().sorted((a, b) -> Long.compare(b.views(), a.views())).limit(5).toList(),
            startDate,
            endDate
        );
    }

    private Optional<Article> getLastArticle() {
        final var response = scanner.getLastArticles();
        final var result = articleParser.parseContent(response.body());
        if (result.isLeft()) {
            userController.notifyAdmin(result.getLeft().getMessage());
            return Optional.empty();
        }
        return result.get().stream().sorted((a, b) -> Long.compare(b.date(), a.date())).filter(it -> !it.isTournament()).findFirst();
    }
}
