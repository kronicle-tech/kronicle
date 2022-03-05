package tech.kronicle.utils;

import java.util.Base64;

public final class BasicAuthUtils {

    public static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    private BasicAuthUtils() {
    }
}
