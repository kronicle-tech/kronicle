package tech.kronicle.service.scanners.gradle.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
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
