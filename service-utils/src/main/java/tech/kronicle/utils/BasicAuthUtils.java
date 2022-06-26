package tech.kronicle.utils;

import java.util.Base64;

public final class BasicAuthUtils {

    private static final Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();

    public static String basicAuth(String username, String password) {
        return "Basic " + BASE_64_ENCODER.encodeToString((username + ":" + password).getBytes());
    }

    private BasicAuthUtils() {
    }
}
