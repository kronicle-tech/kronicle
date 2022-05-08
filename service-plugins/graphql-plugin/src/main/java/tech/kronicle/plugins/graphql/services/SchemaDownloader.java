package tech.kronicle.plugins.graphql.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SchemaDownloader {

    private final GraphQlConfig config;
    private final HttpClient httpClient;

    @SneakyThrows
    public String downloadSchema(String url) {
        HttpResponse<String> response = httpClient.send(
                createHttpRequestBuilder(config.getTimeout())
                        .GET()
                        .uri(URI.create(url))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        return response.body();
    }
}
