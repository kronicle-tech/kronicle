package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.models.HttpHeader;
import tech.kronicle.service.scanners.gradle.config.GradleCustomRepository;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
public class RepositoryAuthHeadersRegistry {

    private final Map<String, List<HttpHeader>> customRepositories;

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

    public List<HttpHeader> getRepositoryAuthHeaders(String url) {
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
