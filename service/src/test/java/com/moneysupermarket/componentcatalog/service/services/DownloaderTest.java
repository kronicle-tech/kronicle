package com.moneysupermarket.componentcatalog.service.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.moneysupermarket.componentcatalog.service.config.DownloaderConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DownloaderTest {
    
    private static final int PORT = 36207;
    private static final Duration TWO_MINUTE_DURATION = Duration.ofMinutes(2);

    @Mock
    private DownloaderConfig config;
    private WebClient webClient;
    @Mock
    private DownloadCache downloadCache;
    @Mock
    private UrlExistsCache urlExistsCache;
    private WireMockServer wireMockServer;
    private Downloader underTest;

    @BeforeEach
    public void beforeEach() {
        webClient = WebClient.create();
        underTest = new Downloader(config, webClient, downloadCache, urlExistsCache, new HttpRequestMaker());
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.stubFor(get(urlPathEqualTo("/download"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("test-output")));
        wireMockServer.stubFor(get(urlPathEqualTo("/delayed"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("delayed-output")
                        .withFixedDelay((int) Duration.ofMinutes(2).toMillis())));
        wireMockServer.stubFor(get(urlPathEqualTo("/not-found"))
                .willReturn(aResponse()
                        .withStatus(404)));
        wireMockServer.stubFor(get(urlPathEqualTo("/moved-permanently"))
                .willReturn(aResponse()
                        .withStatus(301)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(get(urlPathEqualTo("/found"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(get(urlPathEqualTo("/see-other"))
                .willReturn(aResponse()
                        .withStatus(303)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(get(urlPathEqualTo("/redirect-once"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(get(urlPathEqualTo("/redirect-twice"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/redirect-once")));
        wireMockServer.stubFor(get(urlPathEqualTo("/redirect-with-no-location-header"))
                .willReturn(aResponse()
                        .withStatus(302)));
        wireMockServer.stubFor(get(urlPathEqualTo("/internal-server-error"))
                .willReturn(aResponse()
                        .withStatus(500)));
        wireMockServer.stubFor(head(urlPathEqualTo("/download"))
                .willReturn(aResponse()
                        .withStatus(200)));
        wireMockServer.stubFor(head(urlPathEqualTo("/delayed"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay((int) Duration.ofMinutes(2).toMillis())));
        wireMockServer.stubFor(head(urlPathEqualTo("/not-found"))
                .willReturn(aResponse()
                        .withStatus(404)));
        wireMockServer.stubFor(head(urlPathEqualTo("/moved-permanently"))
                .willReturn(aResponse()
                        .withStatus(301)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(head(urlPathEqualTo("/found"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(head(urlPathEqualTo("/see-other"))
                .willReturn(aResponse()
                        .withStatus(303)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(head(urlPathEqualTo("/redirect-once"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/download")));
        wireMockServer.stubFor(head(urlPathEqualTo("/redirect-twice"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:" + PORT + "/redirect-once")));
        wireMockServer.stubFor(head(urlPathEqualTo("/redirect-with-no-location-header"))
                .willReturn(aResponse()
                        .withStatus(302)));
        wireMockServer.stubFor(head(urlPathEqualTo("/internal-server-error"))
                .willReturn(aResponse()
                        .withStatus(500)));
        wireMockServer.start();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
        wireMockServer = null;
    }

    @Test
    public void downloadShouldDownloadAndCache() {
        // Given
        String url = "http://localhost:" + PORT + "/download";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

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
        String url = "http://localhost:" + PORT + "/delayed";
        when(config.getTimeout()).thenReturn(Duration.ofSeconds(1));

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

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
        String url = "http://localhost:" + PORT + "/download";
        when(downloadCache.getContent(url)).thenReturn(Optional.of("cached-output"));

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

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
        String url = "http://localhost:" + PORT + "/not-found";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

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
        String url = "http://localhost:" + PORT + "/moved-permanently";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 1);

        // Then
        verify(downloadCache).putContent("http://localhost:" + PORT + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFollowRedirectAndCacheForFoundResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/found";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 1);

        // Then
        verify(downloadCache).putContent("http://localhost:" + PORT + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFollowRedirectAndCacheSeeOtherResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/see-other";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 1);

        // Then
        verify(downloadCache).putContent("http://localhost:" + PORT + "/download", "test-output");
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isEqualTo("test-output");
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldStopFollowingRedirectsWhenMaxRedirectsOfZeroIsExceeded() {
        // Given
        String url = "http://localhost:" + PORT + "/redirect-once";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

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
        String url = "http://localhost:" + PORT + "/redirect-twice";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 1);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/redirect-once");
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void downloadShouldFailForRedirectWithNoLocationHeader() {
        // Given
        String url = "http://localhost:" + PORT + "/redirect-with-no-location-header";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 1);

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
        String url = "http://localhost:" + PORT + "/internal-server-error";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<String> returnValue = underTest.download(url, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldReturnTrueAndCacheForOkResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/download";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

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
        String url = "http://localhost:" + PORT + "/delayed";
        when(config.getTimeout()).thenReturn(Duration.ofSeconds(1));

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

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
        String url = "http://localhost:" + PORT + "/download";
        when(urlExistsCache.getExists(url)).thenReturn(Optional.of(true));

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

        // Then
        verify(urlExistsCache, never()).putExists(any(), anyBoolean());
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldReturnFalseAndCacheForOkResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/not-found";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

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
        String url = "http://localhost:" + PORT + "/moved-permanently";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 1);

        // Then
        verify(urlExistsCache).putExists("http://localhost:" + PORT + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFollowRedirectAndCacheForFoundResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/found";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 1);

        // Then
        verify(urlExistsCache).putExists("http://localhost:" + PORT + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFollowRedirectAndCacheSeeOtherResponse() {
        // Given
        String url = "http://localhost:" + PORT + "/see-other";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 1);

        // Then
        verify(urlExistsCache).putExists("http://localhost:" + PORT + "/download", true);
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/download");
        assertThat(returnValue.isSuccess()).isTrue();
        assertThat(returnValue.isFailure()).isFalse();
        assertThat(returnValue.getOutput()).isTrue();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldStopFollowingRedirectsWhenMaxRedirectsOfZeroIsExceeded() {
        // Given
        String url = "http://localhost:" + PORT + "/redirect-once";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

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
        String url = "http://localhost:" + PORT + "/redirect-twice";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 1);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo("http://localhost:" + PORT + "/redirect-once");
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }

    @Test
    public void existsShouldFailForRedirectWithNoLocationHeader() {
        // Given
        String url = "http://localhost:" + PORT + "/redirect-with-no-location-header";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 1);

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
        String url = "http://localhost:" + PORT + "/internal-server-error";
        when(config.getTimeout()).thenReturn(TWO_MINUTE_DURATION);

        // When
        Downloader.HttpRequestOutcome<Boolean> returnValue = underTest.exists(url, 0);

        // Then
        assertThat(returnValue.getUrl()).isEqualTo(url);
        assertThat(returnValue.isSuccess()).isFalse();
        assertThat(returnValue.isFailure()).isTrue();
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getExceptions()).isEmpty();
    }
}
