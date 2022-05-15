package ru.homyakin.goodgame.monitoring.service.parser;


import io.vavr.control.Either;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;
import ru.homyakin.goodgame.monitoring.models.EitherError;
import ru.homyakin.goodgame.monitoring.models.ParserError;
import ru.homyakin.goodgame.monitoring.utils.CommonUtils;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;

public class ArticleInfoParser {
    private static final Logger logger = LoggerFactory.getLogger(ArticleInfoParser.class);
    private static final DateTimeFormatter printingFormatter = DateTimeFormatter.ofPattern("d MMMM").localizedBy(new Locale("ru"));

    public static Either<EitherError, ArticleInfo> parseBody(String body) {
        final var doc = Jsoup.parse(body);
        try {
            final var date = parseDate(doc);
            final var views = Long.parseLong(doc.getElementsByClass("news__views").get(0).text());
            final var comments = doc.getElementsByClass("news__comments").get(0).text();
            final var title = doc.getElementsByClass("news-title").get(0).text();
            final var link = doc.getElementsByClass("likely_gg").get(0).attributes().get("data-url");
            return Either.right(new ArticleInfo(
                title,
                link,
                comments,
                date,
                views
            ));
        } catch (Exception e) {
            logger.error("Error parsing", e);
            return Either.left(new ParserError("Error parsing " + CommonUtils.getStringStackTrace(e)));
        }
    }

    private static long parseDate(Document doc) {
        final var dateString = doc.getElementsByClass("news__date").get(0).text()
            .replace("сегодня", LocalDate.now().format(printingFormatter))
            .replace("вчера", LocalDate.now().minus(1, ChronoUnit.DAYS).format(printingFormatter));

        final var parsingFormatter = new DateTimeFormatterBuilder()
            .appendPattern("d MMMM в H:mm")
            .parseDefaulting(ChronoField.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter(new Locale("ru"));
        return LocalDateTime.parse(dateString, parsingFormatter).atZone(DateTimeUtils.moscowZone()).toEpochSecond();
    }
}
