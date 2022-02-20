package tech.kronicle.plugins.gradle.internal.services;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tech.kronicle.common.utils.StringEscapeUtils;
import tech.kronicle.service.models.HttpHeader;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@SpringComponent
@RequiredArgsConstructor
@Slf4j
public class Downloader {

    private final DownloaderConfig config;
    private final WebClient webClient;
    private final DownloadCache downloadCache;
    private final UrlExistsCache urlExistsCache;
    private final HttpRequestMaker httpRequestMaker;

    public HttpRequestOutcome<String> download(String url, List<HttpHeader> headers, int maxRedirectCount) {
        log.debug("Downloading from URL \"" + StringEscapeUtils.escapeString(url) + "\"");

        return makeRequestFollowingRedirects(url, maxRedirectCount,
                originalUrlOrRedirectUrl -> makeRequest(webClient.get(), originalUrlOrRedirectUrl, headers),
                clientResponse -> clientResponse.getStatusCode() == HttpStatus.OK
                        ? Optional.ofNullable(clientResponse.getBody())
                        : Optional.empty(),
                downloadCache::getContent,
                downloadCache::putContent);
    }

    private ResponseEntity<String> makeRequest(WebClient.RequestHeadersUriSpec<?> requestSpec, String originalUrlOrRedirectUrl, List<HttpHeader> headers) {
        return addHeaders(requestSpec.uri(originalUrlOrRedirectUrl), headers)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(
                        WebClientResponseException.class,
                        thrown -> thrown.getStatusCode() == HttpStatus.NOT_FOUND ?
                                Mono.just(new ResponseEntity<>(thrown.getStatusCode())) :
                                Mono.error(thrown)
                )
                .block(config.getTimeout());
    }

    private WebClient.RequestHeadersSpec<?> addHeaders(WebClient.RequestHeadersSpec<?> requestSpec, List<HttpHeader> headers) {
        if (nonNull(headers) && !headers.isEmpty()) {
            return requestSpec.headers(headersSpec -> headers.forEach(
                    header -> headersSpec.add(header.getName(), header.getValue())));
        }
        return requestSpec;
    }

    public HttpRequestOutcome<Boolean> exists(String url, List<HttpHeader> headers, int maxRedirectCount) {
        log.debug("Checking whether URL \"" + StringEscapeUtils.escapeString(url) + "\" exists");

        return makeRequestFollowingRedirects(
                url,
                maxRedirectCount,
                originalUrlOrRedirectUrl -> makeRequest(webClient.head(), originalUrlOrRedirectUrl, headers),
                responseSpec -> {
                    if (responseSpec.getStatusCode() == HttpStatus.OK) {
                        return Optional.of(true);
                    } else if (responseSpec.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Optional.of(false);
                    } else {
                        return Optional.empty();
                    }
                },
                urlExistsCache::getExists,
                urlExistsCache::putExists);
    }

    private <T> HttpRequestOutcome<T> makeRequestFollowingRedirects(
            String url,
            int maxRedirectCount,
            Function<String, ResponseEntity<String>> httpRequest,
            Function<ResponseEntity<String>, Optional<T>> outputGetter,
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
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = httpRequestMaker.makeHttpRequest(httpRequest, url);
            } catch (Exception e) {
                log.error("Failed to make HTTP request for URL \"{}\"", StringEscapeUtils.escapeString(url), e);
                return new HttpRequestOutcome<>(url, false, null, List.of(e));
            }
            log.debug("Response status code was " + responseEntity.getStatusCodeValue());
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.MOVED_PERMANENTLY
                    || statusCode == HttpStatus.FOUND
                    || statusCode == HttpStatus.SEE_OTHER) {
                redirectCount++;

                if (redirectCount <= maxRedirectCount) {
                    url = getLocationHeader(responseEntity);

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
            } else {
                output = outputGetter.apply(responseEntity);

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

    private String getLocationHeader(ResponseEntity<String> clientResponse) {
        List<String> values = clientResponse.getHeaders().get(HttpHeaders.LOCATION);
        return isNull(values) || values.isEmpty() ? null : values.get(0);
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
