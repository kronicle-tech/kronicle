package tech.kronicle.plugins.gradle.internal.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.gradle.PluginPackage;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.pluginutils.scanners.services.ThrowableToScannerErrorMapper;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;

import java.time.Duration;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

    @Bean
    public DownloaderConfig downloaderConfig(GradleConfig gradleConfig) {
        return gradleConfig.getDownloader();
    }

    @Bean
    public DownloadCacheConfig downloadCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getDownloadCache();
    }

    @Bean
    public UrlExistsCacheConfig urlExistsCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getUrlExistsCache();
    }

    @Bean
    public PomCacheConfig pomCacheConfig(GradleConfig gradleConfig) {
        return gradleConfig.getPomCache();
    }

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("http-request-maker", RetryConfig.custom()
                        .maxAttempts(5)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(10), 2))
                        .build())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JsonMapper();
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
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }

}
