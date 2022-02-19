package tech.kronicle.service.utils;

import tech.kronicle.common.utils.CaseUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public final class EnumUtils {

    public static <T extends Enum<T>> List<T> getEnumListFromJsonValues(Class<T> enumType, List<String> values) {
        requireNonNull(enumType, "enumType");
        if (isNull(values)) {
            return List.of();
        }
        return values.stream()
                .map(value -> getEnumFromJsonValue(enumType, value))
                .collect(Collectors.toList());
    }

    public static <T extends Enum<T>> T getEnumFromJsonValue(Class<T> enumType, String value) {
        requireNonNull(enumType, "enumType");
        return Enum.valueOf(enumType, CaseUtils.toScreamingSnakeCase(value));
    }

    private EnumUtils() {
    }
}
