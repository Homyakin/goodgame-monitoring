package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FileUtils {
    public static Document parseHtmlFile(String fileName) {
        try {
            final var file = FileUtils.class.getClassLoader().getResource(fileName).getFile();
            return Jsoup.parse(Files.readString(Paths.get(file)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
     }
}
