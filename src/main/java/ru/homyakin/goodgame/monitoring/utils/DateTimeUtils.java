package ru.homyakin.goodgame.monitoring.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class DateTimeUtils {
    public LocalDateTime moscowTime() {
        return LocalDateTime.now(moscowZone());
    }

    public ZoneId moscowZone() {
        return ZoneId.of("Europe/Moscow");
    }

    public LocalDateTime longToMoscowDateTime(long time) {
        return Instant.ofEpochSecond(time).atZone(moscowZone()).toLocalDateTime();
    }
}
