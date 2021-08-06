package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.config.DownloaderConfig;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class Downloader {

    private final DownloaderConfig config;
    private final WebClient webClient;
    private final DownloadCache downloadCache;
    private final UrlExistsCache urlExistsCache;
    private final HttpRequestMaker httpRequestMaker;

    public HttpRequestOutcome<String> download(String url, int maxRedirectCount) {
        log.debug("Downloading from URL \"" + escapeString(url) + "\"");

        return makeHttpRequestFollowingRedirects(url, maxRedirectCount,
                originalUrlOrRedirectUrl -> webClient.get().uri(originalUrlOrRedirectUrl).exchange().block(config.getTimeout()),
                clientResponse -> clientResponse.statusCode() == HttpStatus.OK
                        ? Optional.of(clientResponse.bodyToMono(String.class).block(config.getTimeout()))
                        : Optional.empty(),
                downloadCache::getContent,
                downloadCache::putContent);
    }

    public HttpRequestOutcome<Boolean> exists(String url, int maxRedirectCount) {
        log.debug("Checking whether URL \"" + escapeString(url) + "\" exists");

        return makeHttpRequestFollowingRedirects(url, maxRedirectCount,
                originalUrlOrRedirectUrl -> webClient.head().uri(originalUrlOrRedirectUrl).exchange().block(config.getTimeout()),
                clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        return Optional.of(true);
                    } else if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Optional.of(false);
                    } else {
                        return Optional.empty();
                    }
                },
                urlExistsCache::getExists,
                urlExistsCache::putExists);
    }

    private <T> HttpRequestOutcome<T> makeHttpRequestFollowingRedirects(String url, int maxRedirectCount,
            Function<String, ClientResponse> httpRequest, Function<ClientResponse, Optional<T>> outputGetter,
            Function<String, Optional<T>> cacheGetter, BiConsumer<String, T> cacheSetter) {
        Optional<T> output = cacheGetter.apply(url);

        if (output.isPresent()) {
            log.debug("Entry found in cache");
            return new HttpRequestOutcome<>(url, true, output.get(), List.of());
        }

        int redirectCount = 0;

        while (true) {
            ClientResponse clientResponse;
            try {
                clientResponse = httpRequestMaker.makeHttpRequest(httpRequest, url);
            } catch (Exception e) {
                log.error("Failed to make HTTP request for URL \"{}\"", escapeString(url), e);
                return new HttpRequestOutcome<>(url, false, null, List.of(e));
            }
            log.debug("Response status code was " + clientResponse.rawStatusCode());
            HttpStatus statusCode = clientResponse.statusCode();
            if (statusCode == HttpStatus.MOVED_PERMANENTLY
                    || statusCode == HttpStatus.FOUND
                    || statusCode == HttpStatus.SEE_OTHER) {
                redirectCount++;

                if (redirectCount <= maxRedirectCount) {
                    url = getLocationHeader(clientResponse);

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
                    log.info("Exceeded max redirect count for URL \"{}\"", escapeString(url));
                    break;
                }
            } else {
                output = outputGetter.apply(clientResponse);

                if (output.isPresent()) {
                    cacheSetter.accept(url, output.get());
                    log.debug("Request succeeded");
                    return new HttpRequestOutcome<>(url, true, output.get(), List.of());
                } else {
                    break;
                }
            }
        }

        log.debug("Request failed");
        return new HttpRequestOutcome<>(url, false, null, List.of());
    }

    private String getLocationHeader(ClientResponse clientResponse) {
        List<String> values = clientResponse.headers().header(HttpHeaders.LOCATION);
        return values.isEmpty() ? null : values.get(0);
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
