package tech.kronicle.plugins.gradle.internal.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tech.kronicle.service.models.HttpHeader;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DownloaderTest {

    private static final Duration TWO_MINUTE_DURATION = Duration.ofMinutes(2);

    @Mock
    private DownloadCache downloadCache;
    @Mock
    private UrlExistsCache urlExistsCache;
    private final DownloaderWireMockFactory wireMockFactory = new DownloaderWireMockFactory();
    private WireMockServer wireMockServer;
    private Downloader underTest;

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
        wireMockServer = null;
    }

    @Test
    public void downloadShouldDownloadAndCache() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DOWNLOAD, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/download";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        verify(downloadCache).putContent(url, "test-output");
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadWhenHeadersAreSuppliedShouldDownloadAndCache() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DOWNLOAD_WITH_HEADERS, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/download-with-headers";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(
                url,
                List.of(
                        new HttpHeader("test-header-1", "test-value-1"),
                        new HttpHeader("test-header-2", "test-value-2")
                ),
                0
        );

        // Then
        verify(downloadCache).putContent(url, "test-output");
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldTimeoutWhenRequestTakesTooLong() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DELAYED, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/delayed";
        createUnderTest(Duration.ofSeconds(1));

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        verify(downloadCache, never()).putContent(any(), any());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).hasSize(1);
        Exception exception;
        exception = returnValue.getExceptions().get(0);
        assertThat(exception).isInstanceOf(IllegalStateException.class);
        assertThat(exception).hasMessage("Timeout on blocking read for 1000000000 NANOSECONDS");
    }

    @Test
    public void downloadShouldUseCacheIfAlreadyCached() {
        // Given
        wireMockServer = wireMockFactory.createWithNoStubs();
        String url = wireMockServer.baseUrl() + "/download";
        when(downloadCache.getContent(url)).thenReturn(Optional.of("cached-output"));
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        verify(downloadCache, never()).putContent(any(), any());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("cached-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldReturnFailureForNotFoundResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.NOT_FOUND, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/not-found";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        verify(downloadCache, never()).putContent(any(), any());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFollowRedirectAndCacheForMovedPermanentlyResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.MOVED_PERMANENTLY, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/moved-permanently";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 1);

        // Then
        verify(downloadCache).putContent(wireMockServer.baseUrl() + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFollowRedirectAndCacheForFoundResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.FOUND, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/found";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 1);

        // Then
        verify(downloadCache).putContent(wireMockServer.baseUrl() + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFollowRedirectAndCacheSeeOtherResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.SEE_OTHER, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/see-other";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 1);

        // Then
        verify(downloadCache).putContent(wireMockServer.baseUrl() + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldStopFollowingRedirectsWhenMaxRedirectsOfZeroIsExceeded() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_ONCE, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/redirect-once";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldStopFollowingRedirectsWhenMaxRedirectsOfOneIsExceeded() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_TWO, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/redirect-twice";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 1);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/redirect-once");
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFailForRedirectWithNoLocationHeader() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_WITH_NO_LOCATION_HEADER, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/redirect-with-no-location-header";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 1);

        // Then
        assertThat(returnValue.getUrl()).isNull();
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFailForFailedRequest() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.INTERNAL_SERVER_ERROR, HttpMethod.GET);
        String url = wireMockServer.baseUrl() + "/internal-server-error";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, null, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).hasSize(1);
        assertThat(returnValue.getExceptions().get(0))
                .isInstanceOf(WebClientResponseException.InternalServerError.class)
                .hasMessage("500 Internal Server Error from GET %s/internal-server-error", wireMockServer.baseUrl());
    }

    @Test
    public void existsWhenHeadersAreSuppliedShouldReturnTrueAndCacheForOkResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DOWNLOAD, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/download";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(
                url,
                List.of(
                        new HttpHeader("test-header-1", "test-value-1"),
                        new HttpHeader("test-header-2", "test-value-2")
                ),
                0
        );

        // Then
        verify(urlExistsCache).putExists(url, true);
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldReturnTrueAndCacheForOkResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DOWNLOAD_WITH_HEADERS, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/download-with-headers";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(
                url,
                List.of(
                        new HttpHeader("test-header-1", "test-value-1"),
                        new HttpHeader("test-header-2", "test-value-2")
                ),
                0
        );

        // Then
        verify(urlExistsCache).putExists(url, true);
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldTimeoutWhenRequestTakesTooLong() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.DELAYED, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/delayed";
        createUnderTest(Duration.ofSeconds(1));

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 0);

        // Then
        verify(downloadCache, never()).putContent(any(), any());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).hasSize(1);
        Exception exception;
        exception = returnValue.getExceptions().get(0);
        assertThat(exception).isInstanceOf(IllegalStateException.class);
        assertThat(exception).hasMessage("Timeout on blocking read for 1000000000 NANOSECONDS");
    }

    @Test
    public void existsShouldUseCacheIfAlreadyCached() {
        // Given
        wireMockServer = wireMockFactory.createWithNoStubs();
        String url = wireMockServer.baseUrl() + "/download";
        when(urlExistsCache.getExists(url)).thenReturn(Optional.of(true));
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 0);

        // Then
        verify(urlExistsCache, never()).putExists(any(), anyBoolean());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldReturnFalseAndCacheForNotFoundResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.NOT_FOUND, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/not-found";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 0);

        // Then
        verify(urlExistsCache).putExists(url, false);
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isFalse();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFollowRedirectAndCacheForMovedPermanentlyResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.MOVED_PERMANENTLY, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/moved-permanently";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 1);

        // Then
        verify(urlExistsCache).putExists(wireMockServer.baseUrl() + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFollowRedirectAndCacheForFoundResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.FOUND, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/found";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 1);

        // Then
        verify(urlExistsCache).putExists(wireMockServer.baseUrl() + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFollowRedirectAndCacheSeeOtherResponse() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.SEE_OTHER, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/see-other";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 1);

        // Then
        verify(urlExistsCache).putExists(wireMockServer.baseUrl() + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldStopFollowingRedirectsWhenMaxRedirectsOfZeroIsExceeded() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_ONCE, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/redirect-once";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldStopFollowingRedirectsWhenMaxRedirectsOfOneIsExceeded() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_TWO, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/redirect-twice";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 1);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(wireMockServer.baseUrl() + "/redirect-once");
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFailForRedirectWithNoLocationHeader() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.REDIRECT_WITH_NO_LOCATION_HEADER, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/redirect-with-no-location-header";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 1);

        // Then
        assertThat(returnValue.getUrl()).isNull();
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFailForFailedRequest() {
        // Given
        wireMockServer = wireMockFactory.create(DownloaderWireMockFactory.Scenario.INTERNAL_SERVER_ERROR, HttpMethod.HEAD);
        String url = wireMockServer.baseUrl() + "/internal-server-error";
        createUnderTest(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, null, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).hasSize(1);
        assertThat(returnValue.getExceptions().get(0))
                .isInstanceOf(WebClientResponseException.InternalServerError.class)
                .hasMessage("500 Internal Server Error from HEAD %s/internal-server-error", wireMockServer.baseUrl());
    }

    private void createUnderTest(Duration timeout) {
        RetryRegistry retryRegistry = RetryRegistry.custom()
                .addRetryConfig("http-request-maker", RetryConfig.ofDefaults())
                .build();
        underTest = new Downloader(new DownloaderConfig(timeout), WebClient.create(), downloadCache, urlExistsCache, new HttpRequestMaker(retryRegistry));
    }
}
