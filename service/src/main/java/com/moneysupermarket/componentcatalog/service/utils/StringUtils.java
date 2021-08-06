package com.moneysupermarket.componentcatalog.service.utils;

import static java.util.Objects.requireNonNull;

public final class StringUtils {

    public static String requireNonEmpty(String value, String name) {
        requireNonNull(value, name);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }

        return value;
    }

    private StringUtils() {
    }
}
