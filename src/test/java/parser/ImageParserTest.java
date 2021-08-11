package parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.homyakin.goodgame.monitoring.news.service.parser.ImageParser;
import utils.FileUtils;

public class ImageParserTest {

    private static final ImageParser imageParser = new ImageParser();

    @Test
    void getNewsGifLink() {
        var doc = FileUtils.parseHtmlFile("news_with_gif.html");
        var element = doc.getElementsByClass("news-element");
        var link = imageParser.getImageLink(element.first());
        Assertions.assertEquals("https://static.goodgame.ru/files/logotypes/ma_crop_42069_Pltb.gif", link);
    }

    @Test
    void getNewsImageLink() {
        var doc = FileUtils.parseHtmlFile("news_with_image.html");
        var element = doc.getElementsByClass("news-element");
        var link = imageParser.getImageLink(element.first());
        Assertions.assertEquals("https://static.goodgame.ru/files/logotypes/ma_crop_42100_ve4s.webp", link);
    }

    @Test
    void getTournamentImageLink() {
        var doc = FileUtils.parseHtmlFile("tournament_with_image.html");
        var element = doc.getElementsByClass("news-element");
        var link = imageParser.getImageLink(element.first());
        Assertions.assertEquals("https://goodgame.ru/files/logotypes/cp_9688_JmiR_cup.jpg", link);
    }
}
