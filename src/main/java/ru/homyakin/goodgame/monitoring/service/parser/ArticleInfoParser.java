package ru.homyakin.goodgame.monitoring.service.parser;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.vavr.control.Either;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homyakin.goodgame.monitoring.models.ApiArticle;
import ru.homyakin.goodgame.monitoring.models.ApiComments;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.GoodGameArticle;
import ru.homyakin.goodgame.monitoring.models.ParserError;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;

public class ArticleInfoParser {
    private static final Logger logger = LoggerFactory.getLogger(ArticleInfoParser.class);

    private final static ObjectMapper objectMapper = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    public static Either<EitherError, ApiArticle> parseApiResponse(HttpResponse<String> articleResponse) {
        try {
            return Either.right(objectMapper.readValue(articleResponse.body(), ApiArticle.class));
        } catch (Exception e) {
            logger.error("Error parsing", e);
            return Either.left(new ParserError("Error parsing " + CommonUtils.getStringStackTrace(e)));
        }
    }

    public static Either<EitherError, ApiComments> parseApiComments(HttpResponse<String> commentsResponse) {
        try {
            return Either.right(objectMapper.readValue(commentsResponse.body(), ApiComments.class));
        } catch (Exception e) {
            logger.error("Error parsing", e);
            return Either.left(new ParserError("Error parsing " + CommonUtils.getStringStackTrace(e)));
        }
    }
}
