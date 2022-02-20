package tech.kronicle.plugins.gradle.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties("gradle")
@ConstructorBinding
@Value
@NonFinal
public class GradleConfig {

    List<String> additionalSafeSoftwareRepositoryUrls;
    List<GradleCustomRepository> customRepositories;
    @NotNull
    DownloaderConfig downloader;
    @NotNull
    DownloadCacheConfig downloadCache;
    @NotNull
    UrlExistsCacheConfig urlExistsCache;
    @NotNull
    PomCacheConfig pomCache;
}
