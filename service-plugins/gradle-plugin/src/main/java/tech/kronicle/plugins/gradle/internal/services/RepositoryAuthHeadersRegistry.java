package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.HttpHeaderConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RepositoryAuthHeadersRegistry {

    private final Map<String, List<HttpHeaderConfig>> customRepositories;

    @Inject
    public RepositoryAuthHeadersRegistry(GradleConfig config) {
        this.customRepositories = Optional.ofNullable(config.getCustomRepositories())
                .orElse(List.of())
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        it -> ensureUrlHasATrailingSlash(it.getUrl()),
                        gradleCustomRepository -> Optional.ofNullable(gradleCustomRepository.getHttpHeaders())
                                .orElse(List.of())
                ));
    }

    public List<HttpHeaderConfig> getRepositoryAuthHeaders(String url) {
        return customRepositories.entrySet().stream()
                .filter(it -> url.startsWith(it.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private static String ensureUrlHasATrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }
}
