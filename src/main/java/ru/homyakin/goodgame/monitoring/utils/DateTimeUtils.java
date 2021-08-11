package ru.homyakin.goodgame.monitoring.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {
    public static LocalDateTime moscowTime() {
        return LocalDateTime.now(moscowZone());
    }

    public static ZoneId moscowZone() {
        return ZoneId.of("Europe/Moscow");
    }

    public static LocalDateTime longToMoscowDateTime(long time) {
        return Instant.ofEpochSecond(time).atZone(moscowZone()).toLocalDateTime();
    }
}
