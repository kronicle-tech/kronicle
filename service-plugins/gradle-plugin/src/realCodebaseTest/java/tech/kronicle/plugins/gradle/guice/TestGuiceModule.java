package tech.kronicle.plugins.gradle.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.GradleCustomRepository;
import tech.kronicle.plugins.gradle.config.HttpHeaderConfig;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class TestGuiceModule extends AbstractModule {

    private final String testDataDir;

    public TestGuiceModule(Class<?> testClass) {
        testDataDir = "build/test-data/" + testClass.getName();
    }

    @Provides
    public DownloaderConfig downloaderConfig() {
        return new DownloaderConfig(Duration.ofSeconds(60));
    }

    @Provides
    public DownloadCacheConfig downloadCacheConfig() {
        return new DownloadCacheConfig(testDataDir + "/download-cache");
    }

    @Provides
    public UrlExistsCacheConfig urlExistsCacheConfig() {
        return new UrlExistsCacheConfig(testDataDir + "/url-exists-cache");
    }

    @Provides
    public PomCacheConfig pomCacheConfig() {
        return new PomCacheConfig(testDataDir + "/gradle/pom-cache");
    }

    @Provides
    public GradleConfig gradleConfig(DownloaderConfig downloaderConfig, DownloadCacheConfig downloadCacheConfig, UrlExistsCacheConfig urlExistsCache, PomCacheConfig pomCacheConfig) {
        return new GradleConfig(
                List.of("http://localhost:36211/repo-with-authentication/"),
                List.of(
                        new GradleCustomRepository("someCustomRepository", "https://example.com/repo/", List.of()),
                        new GradleCustomRepository("someCustomRepositoryWithAuthentication", "http://localhost:36211/repo-with-authentication/", List.of(
                                new HttpHeaderConfig("test-header-1", "test-value-1"),
                                new HttpHeaderConfig("test-header-2", "test-value-2")
                        ))
                ),
                downloaderConfig,
                downloadCacheConfig,
                urlExistsCache,
                pomCacheConfig
        );
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
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("http-request-maker", RetryConfig.ofDefaults())
                .build();
    }

    @Provides
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }
}
