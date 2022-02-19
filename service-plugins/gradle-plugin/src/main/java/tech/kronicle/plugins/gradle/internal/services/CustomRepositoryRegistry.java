package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.GradleCustomRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomRepositoryRegistry {

    private final Map<String, String> customRepositoryUrls;

    public CustomRepositoryRegistry(GradleConfig config) {
        customRepositoryUrls = Optional.ofNullable(config.getCustomRepositories())
                .map(items -> items.stream()
                        .collect(Collectors.toMap(
                                GradleCustomRepository::getName,
                                GradleCustomRepository::getUrl)))
                .orElse(Map.of());
    }

    public String getCustomRepositoryUrl(String name) {
        return customRepositoryUrls.get(name);
    }
}
