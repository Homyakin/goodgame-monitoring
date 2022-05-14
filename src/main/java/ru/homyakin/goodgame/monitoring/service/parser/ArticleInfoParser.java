package ru.homyakin.goodgame.monitoring.service.parser;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homyakin.goodgame.monitoring.models.ArticleInfo;

public class ArticleInfoParser {
    private static final Logger logger = LoggerFactory.getLogger(ArticleInfoParser.class);

    public static Optional<ArticleInfo> parseBody(String body, String link) {
        final var doc = Jsoup.parse(body);
        try {
            final var date = parseDate(doc);
            final var views = Long.parseLong(doc.getElementsByClass("news__views").get(0).text());
            final var comments = doc.getElementsByClass("news__comments").get(0).text();
            final var title = doc.getElementsByClass("news-title").get(0).text();
            return Optional.of(new ArticleInfo(
                title,
                link,
                comments,
                date,
                views
            ));
        } catch (Exception e) {
            logger.error("Error parsing", e);
            return Optional.empty();
        }
    }

    private static long parseDate(Document doc) {
        final var printingFormatter = DateTimeFormatter.ofPattern("d MMMM").localizedBy(new Locale("ru"));
        final var dateString = doc.getElementsByClass("news__date").get(0).text()
            .replace("сегодня", LocalDate.now().format(printingFormatter))
            .replace("вчера", LocalDate.now().format(printingFormatter));

        final var parsingFormatter = new DateTimeFormatterBuilder()
            .appendPattern("d MMMM в H:mm")
            .parseDefaulting(ChronoField.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter(new Locale("ru"));
        return LocalDateTime.parse(dateString, parsingFormatter).toEpochSecond(ZoneOffset.ofHours(3));
    }
}
