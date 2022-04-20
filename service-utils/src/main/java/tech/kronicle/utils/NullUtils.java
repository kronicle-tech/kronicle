package tech.kronicle.utils;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class NullUtils {

    public static <T> T firstNonNull(Stream<T> values) {
        requireNonNull(values, "values");
        return values
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private NullUtils() {
    }
}
