package ru.homyakin.goodgame.monitoring.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static LocalDateTime moscowTime() {
        return LocalDateTime.now(moscowZone());
    }

    public static ZoneId moscowZone() {
        return ZoneId.of("Europe/Moscow");
    }

    public static LocalDateTime longToMoscowDateTime(long time) {
        return Instant.ofEpochSecond(time).atZone(moscowZone()).toLocalDateTime();
    }

    public static String longToTimeString(long time) {
        return longToMoscowDateTime(time).format(formatter);
    }
}
