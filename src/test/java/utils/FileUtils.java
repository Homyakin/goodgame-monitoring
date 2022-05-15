package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static String readFile(String fileName) {
        try {
            final var file = FileUtils.class.getClassLoader().getResource(fileName).getFile();
            return Files.readString(Paths.get(file));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
     }
}
