package ru.homyakin.goodgame.monitoring.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.vavr.control.Either;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homyakin.goodgame.monitoring.models.Article;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.GoodGameArticle;
import ru.homyakin.goodgame.monitoring.models.ParserError;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;

public class ArticleParser {
    private final static Logger logger = LoggerFactory.getLogger(ArticleParser.class);
    private final static Pattern jsonStart = Pattern.compile("\\[\\{");
    private final static Pattern jsonEnd = Pattern.compile("}]");
    private final static ObjectMapper objectMapper = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    public static Either<EitherError, List<Article>> parseContent(String html) {
        final var json = getArticlesJsonFromHtml(html);
        try {
            return Either.right(
                Arrays.stream(objectMapper.readValue(json, GoodGameArticle[].class))
                .map(GoodGameArticle::toArticle)
                .toList()
            );
        } catch (Exception e) {
            logger.error("Unable to parse content", e);
            return Either.left(new ParserError("Unable to parse content " + CommonUtils.getStringStackTrace(e)));
        }
    }

    private static String getArticlesJsonFromHtml(String html) {
        final var doc = Jsoup.parse(html);
        final var function = doc.getElementsByClass("news-block")
            .get(0)
            .getElementsByTag("script")
            .get(0)
            .childNode(0)
            .toString();
        var matcher = jsonStart.matcher(function);
        matcher.find();
        var jsonStartIndex = matcher.start();
        matcher = jsonEnd.matcher(function);
        int jsonEndIndex = 0;
        while (matcher.find()) {
            jsonEndIndex = matcher.end();
        }
        return StringEscapeUtils.unescapeJava(
            function.substring(jsonStartIndex, jsonEndIndex)
                .replace("\\\"", "\\\\\"") // unescapeJava убирает экранирование для того, чтобы распарсить Unicode
                .replace("\\n", "\\\\n") // но вместе с этим убирает и остальное экранирование, поэтому нужно добавить
        );
    }
}
