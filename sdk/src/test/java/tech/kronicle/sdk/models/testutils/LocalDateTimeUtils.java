package tech.kronicle.sdk.models.testutils;

import java.time.LocalDateTime;

public final class LocalDateTimeUtils {

    public static LocalDateTime createLocalDateTime(int yearNumber) {
        return createLocalDateTime(yearNumber, 1);
    }

    public static LocalDateTime createLocalDateTime(int yearNumber, int monthNumber) {
        return LocalDateTime.of(2000 + yearNumber, monthNumber, 1, 0, 0);
    }

    private LocalDateTimeUtils() {
    }
}
