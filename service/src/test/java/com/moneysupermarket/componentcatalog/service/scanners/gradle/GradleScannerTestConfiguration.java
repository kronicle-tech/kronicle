package com.moneysupermarket.componentcatalog.service.scanners.gradle;

import com.moneysupermarket.componentcatalog.service.config.DownloadCacheConfig;
import com.moneysupermarket.componentcatalog.service.config.DownloaderConfig;
import com.moneysupermarket.componentcatalog.service.config.UrlExistsCacheConfig;
import com.moneysupermarket.componentcatalog.service.mappers.ThrowableToScannerErrorMapper;
import com.moneysupermarket.componentcatalog.service.services.DownloadCache;
import com.moneysupermarket.componentcatalog.service.services.Downloader;
import com.moneysupermarket.componentcatalog.service.services.HttpRequestMaker;
import com.moneysupermarket.componentcatalog.service.services.UrlExistsCache;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
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
