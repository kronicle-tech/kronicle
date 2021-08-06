package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepository;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepositoryScope;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareRepositoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
