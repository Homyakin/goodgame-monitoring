package ru.homyakin.goodgame.monitoring.article.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.homyakin.goodgame.monitoring.article.service.parser.ArticleParser;
import ru.homyakin.goodgame.monitoring.article.web.ArticleScanner;
import ru.homyakin.goodgame.monitoring.telegram.ChannelController;

@Service
public class ArticleMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(ArticleMonitoring.class);
    private final ArticleScanner articleScanner;
    private final ArticleStorage storage;
    private final ArticleParser articleParser;
    private final ChannelController channelController;
    private Long lastArticleDate = null;

    public ArticleMonitoring(
        ArticleScanner articleScanner,
        ArticleStorage storage,
        ArticleParser articleParser,
        ChannelController channelController
    ) {
        this.articleScanner = articleScanner;
        this.storage = storage;
        this.articleParser = articleParser;
        this.channelController = channelController;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitor() {
        var response = articleScanner.getLastArticles();
        var articles = articleParser.parseContent(response.body());
        if (lastArticleDate == null) {
            logger.info("Initialized monitoring");
            lastArticleDate = articles.get(0).getDate();
        }

        int lastIdx = 0;
        while (lastIdx < articles.size() && lastArticleDate < articles.get(lastIdx).getDate()) {
            ++lastIdx;
        }
        if (lastIdx != 0) {
            logger.info("Got {} new articles", lastIdx);
        }
        for (int i = lastIdx - 1; i >= 0; --i) {
            var article = articles.get(i);
            var message = channelController.sendArticle(
                article,
                storage.getArticle(article.getLink()).orElse(null)
            );
            if (message.isPresent() && !article.isTournament()) {
                storage.insertArticle(article.getLink(), message.get());
            } else if (message.isEmpty()) {
                // TODO отправить сообщение админу
                logger.error("Something wrong with {}", article.getLink());
            }
            lastArticleDate = article.getDate();
        }
    }

}
