package ru.homyakin.goodgame.monitoring.article.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.article.service.parser.ArticleParser;
import ru.homyakin.goodgame.monitoring.article.web.ArticleScanner;
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
    private Long lastArticleDate = null;

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
        var response = articleScanner.getLastArticles();
        var articles = articleParser.parseContent(response.body());
        if (lastArticleDate == null) {
            logger.info("Initialized monitoring");
            lastArticleDate = articles.get(0).date();
        }

        int lastIdx = 0;
        while (lastIdx < articles.size() && lastArticleDate < articles.get(lastIdx).date()) {
            ++lastIdx;
        }
        if (lastIdx != 0) {
            logger.info("Got {} new articles", lastIdx);
        }
        for (int i = lastIdx - 1; i >= 0; --i) {
            var article = articles.get(i);
            var result = channelController.sendArticle(
                article,
                storage.getArticle(article.link()).orElse(null)
            );
            result.peek(message -> {
                if (!article.tournament()) {
                    storage.insertArticle(article.link(), message);
                }
            }).peekLeft(error -> {
                logger.error("Something wrong with {}", article.link());
                userController.notifyAdmin(error.getMessage());
            });
            lastArticleDate = article.date();
        }
    }

}
