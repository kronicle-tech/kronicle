package tech.kronicle.plugins.gradle.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.pluginutils.FileUtils;
import tech.kronicle.pluginutils.ThrowableToScannerErrorMapper;

import java.net.http.HttpClient;
import java.time.Duration;

import static tech.kronicle.pluginutils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.pluginutils.HttpClientFactory.createHttpClient;
import static tech.kronicle.pluginutils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public DownloaderConfig downloaderConfig(GradleConfig gradleConfig) {
        return gradleConfig.getDownloader();
    }

    @Provides
    public DownloadCacheConfig downloadCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getDownloadCache();
    }

    @Provides
    public UrlExistsCacheConfig urlExistsCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getUrlExistsCache();
    }

    @Provides
    public PomCacheConfig pomCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getPomCache();
    }

    @Provides
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("http-request-maker", RetryConfig.custom()
                        .maxAttempts(5)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(10), 2))
                        .build())
                .build();
    }

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }

    @Provides
    public HttpClient httpClient() {
        return createHttpClient();
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }

    @Provides
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }
}
