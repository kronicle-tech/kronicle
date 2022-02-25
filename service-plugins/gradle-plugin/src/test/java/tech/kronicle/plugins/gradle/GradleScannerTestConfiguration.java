package tech.kronicle.plugins.gradle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.GradleCustomRepository;
import tech.kronicle.plugins.gradle.config.HttpHeaderConfig;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.pluginutils.scanners.services.ThrowableToScannerErrorMapper;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;

import java.time.Duration;
import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = GradleScanner.class)
public class GradleScannerTestConfiguration {

    @Bean
    public TestDataDir testDataDir(@Value("${test-name}") String testName) {
        return new TestDataDir(testName);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }

    @Bean
    public DownloaderConfig downloaderConfig() {
        return new DownloaderConfig(Duration.ofSeconds(60));
    }

    @Bean
    public DownloadCacheConfig downloadCacheConfig(TestDataDir testDataDir) {
        return new DownloadCacheConfig(testDataDir.getValue() + "/download-cache");
    }

    @Bean
    public UrlExistsCacheConfig urlExistsCacheConfig(TestDataDir testDataDir) {
        return new UrlExistsCacheConfig(testDataDir.getValue() + "/url-exists-cache");
    }

    @Bean
    public PomCacheConfig pomCacheConfig(TestDataDir testDataDir) {
        return new PomCacheConfig(testDataDir.getValue() + "/gradle/pom-cache");
    }

    @Bean
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

    @Bean
    public FileUtils fileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("http-request-maker", RetryConfig.ofDefaults())
                .build();
    }

    @Bean
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }

}
