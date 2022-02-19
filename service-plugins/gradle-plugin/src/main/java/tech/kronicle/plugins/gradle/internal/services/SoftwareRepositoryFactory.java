package tech.kronicle.plugins.gradle.internal.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.SoftwareRepositoryScope;
import tech.kronicle.sdk.models.SoftwareRepositoryType;

@Component
@RequiredArgsConstructor
public class SoftwareRepositoryFactory {

    private final SoftwareRepositoryUrlSafetyChecker urlSafetyChecker;

    public SoftwareRepository createSoftwareRepository(String scannerId, String url, SoftwareRepositoryScope scope) {
        return SoftwareRepository.builder()
                .scannerId(scannerId)
                .type(SoftwareRepositoryType.MAVEN)
                .url(url)
                .safe(urlSafetyChecker.isSoftwareRepositoryUrlSafe(url))
                .scope(scope)
                .build();
    }
}
