package tech.kronicle.sdk.models.testutils;

import java.time.LocalDateTime;

public final class LocalDateTimeUtils {

    public static LocalDateTime createLocalDateTime(int localDateTimeNumber) {
        return LocalDateTime.of(2000 + localDateTimeNumber, 1, 1, 0, 0);
    }

    private LocalDateTimeUtils() {
    }
}
