package tech.kronicle.plugins.gradle.internal.services;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.HttpHeaderConfig;
import tech.kronicle.pluginutils.HttpMethods;
import tech.kronicle.pluginutils.HttpStatuses;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.pluginutils.HttpClientFactory.createHttpRequestBuilder;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class Downloader {

    private final HttpClient httpClient;
    private final DownloaderConfig config;
    private final DownloadCache downloadCache;
    private final UrlExistsCache urlExistsCache;
    private final HttpRequestMaker httpRequestMaker;

    public HttpRequestOutcome<String> download(String url, List<HttpHeaderConfig> headers, int maxRedirectCount) {
        log.debug("Downloading from URL \"" + StringEscapeUtils.escapeString(url) + "\"");

        return makeRequestFollowingRedirects(
                HttpMethods.GET,
                url, 
                maxRedirectCount,
                originalUrlOrRedirectUrl -> makeRequest(createHttpRequestBuilder(config.getTimeout()), originalUrlOrRedirectUrl, headers),
                response -> response.statusCode() == HttpStatuses.OK
                        ? Optional.ofNullable(response.body())
                        : Optional.empty(),
                downloadCache::getContent,
                downloadCache::putContent);
    }

    @SneakyThrows
    private HttpResponse<String> makeRequest(HttpRequest.Builder requestBuilder, String originalUrlOrRedirectUrl, List<HttpHeaderConfig> headers) {
        addHeaders(requestBuilder.uri(URI.create(originalUrlOrRedirectUrl)), headers);
        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private void addHeaders(HttpRequest.Builder requestBuilder, List<HttpHeaderConfig> headers) {
        if (nonNull(headers) && !headers.isEmpty()) {
            headers.forEach(header -> requestBuilder.header(header.getName(), header.getValue()));
        }
    }

    public HttpRequestOutcome<Boolean> exists(String url, List<HttpHeaderConfig> headers, int maxRedirectCount) {
        log.debug("Checking whether URL \"" + StringEscapeUtils.escapeString(url) + "\" exists");

        return makeRequestFollowingRedirects(
                HttpMethods.HEAD,
                url,
                maxRedirectCount,
                originalUrlOrRedirectUrl -> makeRequest(
                        createHttpRequestBuilder(config.getTimeout()).method(HttpMethods.HEAD, HttpRequest.BodyPublishers.noBody()),
                        originalUrlOrRedirectUrl,
                        headers
                ),
                response -> {
                    if (response.statusCode() == HttpStatuses.OK) {
                        return Optional.of(true);
                    } else if (response.statusCode() == HttpStatuses.NOT_FOUND) {
                        return Optional.of(false);
                    } else {
                        return Optional.empty();
                    }
                },
                urlExistsCache::getExists,
                urlExistsCache::putExists);
    }

    private <T> HttpRequestOutcome<T> makeRequestFollowingRedirects(
            String httpMethod,
            String url,
            int maxRedirectCount,
            Function<String, HttpResponse<String>> httpRequest,
            Function<HttpResponse<String>, Optional<T>> outputGetter,
            Function<String, Optional<T>> cacheGetter,
            BiConsumer<String, T> cacheSetter
    ) {
        Optional<T> output = cacheGetter.apply(url);

        if (output.isPresent()) {
            log.debug("Entry found in cache");
            return new HttpRequestOutcome<>(url, true, output.get(), List.of());
        }

        int redirectCount = 0;

        while (true) {
            HttpResponse<String> response;
            try {
                response = httpRequestMaker.makeHttpRequest(httpRequest, url);
            } catch (Exception e) {
                log.error("Failed to make HTTP request for URL \"{}\"", StringEscapeUtils.escapeString(url), e);
                return new HttpRequestOutcome<>(url, false, null, List.of(e));
            }
            log.debug("Response status code was " + response.statusCode());
            int statusCode = response.statusCode();
            if (statusCode == HttpStatuses.MOVED_PERMANENTLY
                    || statusCode == HttpStatuses.FOUND
                    || statusCode == HttpStatuses.SEE_OTHER) {
                redirectCount++;

                if (redirectCount <= maxRedirectCount) {
                    url = getLocationHeader(response);

                    if (isNull(url)) {
                        log.warn("Redirect response is missing a Location HTTP response header");
                        break;
                    } else {
                        output = cacheGetter.apply(url);

                        if (output.isPresent()) {
                            log.debug("Entry found in cache");
                            return new HttpRequestOutcome<>(url, true, output.get(), List.of());
                        }
                    }
                } else {
                    log.info("Exceeded max redirect count for URL \"{}\"", StringEscapeUtils.escapeString(url));
                    break;
                }
            } else if (statusCode == HttpStatuses.OK || statusCode == HttpStatuses.NOT_FOUND) {
                output = outputGetter.apply(response);

                if (output.isPresent()) {
                    cacheSetter.accept(url, output.get());
                    log.debug("Request succeeded");
                    return new HttpRequestOutcome<>(url, true, output.get(), List.of());
                } else {
                    break;
                }
            } else{
                DownloaderException exception = new DownloaderException(
                        httpMethod,
                        url,
                        statusCode,
                        response.body()
                );
                log.warn(exception.getMessage());
                return new HttpRequestOutcome<>(url, false, null, List.of(exception));
            }
        }

        log.debug("Request failed");
        return new HttpRequestOutcome<>(url, false, null, List.of());
    }

    private String getLocationHeader(HttpResponse<String> response) {
        return response.headers().firstValue(HttpHeaders.LOCATION).orElse(null);
    }

    @Value
    public static class HttpRequestOutcome<T> {

        String url;
        boolean success;
        T output;
        List<Exception> exceptions;

        public boolean isFailure() {
            return !success;
        }
    }
}
