package tech.kronicle.utils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public final class HttpClientFactory {

    public static HttpClient createHttpClient() {
        return createHttpClient(Duration.ofSeconds(60));
    }

    public static HttpClient createHttpClient(Duration timeout) {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(timeout)
                .build();
    }

    public static HttpRequest.Builder createHttpRequestBuilder(Duration timeout) {
        return HttpRequest.newBuilder()
                .timeout(timeout);
    }

    private HttpClientFactory() {
    }
}
