package parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.homyakin.goodgame.monitoring.service.parser.ArticleInfoParser;
import ru.homyakin.goodgame.monitoring.utils.DateTimeUtils;
import utils.FileUtils;

public class ArticleInfoParserTest {

    @Test
    public void parseTodayArticle() {
        var body = FileUtils.readFile("today_article_info.html");
        var articleInfo = ArticleInfoParser.parseBody(body).get();

        Assertions.assertEquals("Пример заголовка", articleInfo.title());
        Assertions.assertEquals("11 комментариев", articleInfo.comments());
        Assertions.assertEquals("https://goodgame.ru/news/32551", articleInfo.link());
        Assertions.assertEquals(LocalDate.now().atStartOfDay(DateTimeUtils.moscowZone()).toEpochSecond(), articleInfo.date());
        Assertions.assertEquals(1806, articleInfo.views());
    }

    @Test
    public void parseYesterdayArticle() {
        var body = FileUtils.readFile("yesterday_article_info.html");
        var articleInfo = ArticleInfoParser.parseBody(body).get();

        Assertions.assertEquals("Пример заголовка", articleInfo.title());
        Assertions.assertEquals("11 комментариев", articleInfo.comments());
        Assertions.assertEquals("https://goodgame.ru/news/32551", articleInfo.link());
        Assertions.assertEquals(
            LocalDate.now().minus(1, ChronoUnit.DAYS).atStartOfDay(DateTimeUtils.moscowZone()).toEpochSecond(),
            articleInfo.date()
        );
        Assertions.assertEquals(1806, articleInfo.views());
    }

    @Test
    public void parseArticleWithDate() {
        var body = FileUtils.readFile("date_article_info.html");
        var articleInfo = ArticleInfoParser.parseBody(body).get();

        Assertions.assertEquals("Пример заголовка", articleInfo.title());
        Assertions.assertEquals("11 комментариев", articleInfo.comments());
        Assertions.assertEquals("https://goodgame.ru/news/32551", articleInfo.link());
        Assertions.assertEquals(
            LocalDateTime
                .of(Calendar.getInstance().get(Calendar.YEAR), 12, 31, 23, 59, 0)
                .atZone(DateTimeUtils.moscowZone())
                .toEpochSecond(),
            articleInfo.date()
        );
        Assertions.assertEquals(1806, articleInfo.views());
    }
}
