package ru.homyakin.goodgame.monitoring.web;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.homyakin.goodgame.monitoring.service.NewsStorage;
import ru.homyakin.goodgame.monitoring.telegram.Bot;
import ru.homyakin.goodgame.monitoring.telegram.BotConfiguration;
import ru.homyakin.goodgame.monitoring.web.models.News;

@Service
public class NewsMonitoring {
    private final static Logger logger = LoggerFactory.getLogger(NewsMonitoring.class);
    private final NewsScanner newsScanner;
    private final Bot bot;
    private final String channel;
    private final NewsStorage storage;
    private Long lastNewsDate = null;

    public NewsMonitoring(NewsScanner newsScanner, Bot bot, BotConfiguration botConfiguration, NewsStorage storage) {
        this.newsScanner = newsScanner;
        this.bot = bot;
        this.channel = botConfiguration.getChannel();
        this.storage = storage;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void monitorNews() {
        var news = newsScanner.getLastNews();
        if (lastNewsDate == null) {
            logger.info("Initialized monitoring");
            lastNewsDate = news.get(0).getDate();
        }

        int lastIdx = 0;
        while (lastIdx < news.size() && lastNewsDate < news.get(lastIdx).getDate()) {
            ++lastIdx;
        }
        if (lastIdx != 0) {
            logger.info("Got {} new news", lastIdx);
        }
        for (int i = lastIdx - 1; i >= 0; --i) {
            Optional<Message> message;
            try {
                if ((message = storage.getNews(news.get(i).getLink())).isPresent()) {
                    var m = message.get();
                    if (m.getCaption() != null) {
                        message = bot.editMessage(createEditMessageCaption(m, news.get(i)));
                    } else {
                        message = bot.editMessage(createEditMessageText(m, news.get(i)));
                    }
                } else {
                    message = bot.sendMessage(creteSendPhotoFromNews(news.get(i)));
                }
            } catch (IOException e) {
                logger.error("Error during sending photo", e);
                message = bot.sendMessage(createMessageFromNews(news.get(i)));
            }
            if (message.isPresent()) {
                storage.insertNews(news.get(i).getLink(), message.get());
            }
            lastNewsDate = news.get(i).getDate();
        }
    }

    private EditMessageCaption createEditMessageCaption(Message message, News news) {
        return new EditMessageCaption()
            .setChatId(channel)
            .setMessageId(message.getMessageId())
            .setCaption(news.toString());
    }

    private EditMessageText createEditMessageText(Message message, News news) {
        return new EditMessageText()
            .setChatId(channel)
            .setMessageId(message.getMessageId())
            .setText(news.toString())
            .disableWebPagePreview();
    }

    private SendMessage createMessageFromNews(News news) {
        return new SendMessage()
            .setChatId(channel)
            .disableWebPagePreview()
            .setText(news.toString());
    }

    private SendPhoto creteSendPhotoFromNews(News news) throws IOException {
        return new SendPhoto()
            .setPhoto(news.getLink(), new URL(news.getImageLink()).openStream())
            .setChatId(channel)
            .setCaption(news.toString());
    }
}
