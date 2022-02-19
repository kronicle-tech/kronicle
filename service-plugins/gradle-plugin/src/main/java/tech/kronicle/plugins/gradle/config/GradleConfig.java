package tech.kronicle.plugins.gradle.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
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
