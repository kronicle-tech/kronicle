package tech.kronicle.common;

import static java.util.Objects.isNull;

public final class StringEscapeUtils {

    public static String escapeString(String value) {
        if (isNull(value)) {
            return null;
        }

        return value.replace("\"", "\\\"");
    }

    private StringEscapeUtils() {
    }
}
