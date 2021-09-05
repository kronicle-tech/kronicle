package tech.kronicle.service.scanners.gradle;

import tech.kronicle.service.config.DownloadCacheConfig;
import tech.kronicle.service.config.DownloaderConfig;
import tech.kronicle.service.config.UrlExistsCacheConfig;
import tech.kronicle.service.mappers.ThrowableToScannerErrorMapper;
import tech.kronicle.service.services.DownloadCache;
import tech.kronicle.service.services.Downloader;
import tech.kronicle.service.services.HttpRequestMaker;
import tech.kronicle.service.services.UrlExistsCache;
import tech.kronicle.service.utils.FileUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;

@EnableAutoConfiguration
@ComponentScan
public class GradleScannerTestConfiguration {

    @Bean
    public FileUtils fileUtils() {
        return new FileUtils();
    }

    @Bean
    public DownloadCache downloadCache(FileUtils fileUtils, DownloadCacheConfig config) throws IOException {
        return new DownloadCache(fileUtils, config);
    }

    @Bean
    public UrlExistsCache urlExistsCache(FileUtils fileUtils, UrlExistsCacheConfig config) throws IOException {
        return new UrlExistsCache(fileUtils, config);
    }

    @Bean
    public HttpRequestMaker httpRequestMaker() {
        return new HttpRequestMaker();
    }

    @Bean
    public Downloader downloader(WebClient webClient, DownloadCache downloadCache, UrlExistsCache urlExistsCache, HttpRequestMaker httpRequestMaker) {
        return new Downloader(new DownloaderConfig(Duration.ofMinutes(2)), webClient, downloadCache, urlExistsCache, httpRequestMaker);
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    @Bean
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }
}
