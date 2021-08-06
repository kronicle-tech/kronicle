package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.config.GradleConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.SAFE_REPOSITORY_URLS;

@Service
public class SoftwareRepositoryUrlSafetyChecker {

    private final List<String> safeSoftwareRepositoryUrls;

    public SoftwareRepositoryUrlSafetyChecker(GradleConfig config) {
        safeSoftwareRepositoryUrls = createSafeSoftwareRepositoryUrls(config);
    }

    public boolean isSoftwareRepositoryUrlSafe(String softwareRepositoryUrl) {
        softwareRepositoryUrl = ensureUrlHasTrailingSlash(softwareRepositoryUrl);
        return safeSoftwareRepositoryUrls.stream().anyMatch(softwareRepositoryUrl::startsWith);
    }

    private List<String> createSafeSoftwareRepositoryUrls(GradleConfig config) {
        return Stream.concat(SAFE_REPOSITORY_URLS.stream(), Optional.ofNullable(config.getAdditionalSafeSoftwareRepositoryUrls()).stream().flatMap(Collection::stream))
                .map(this::ensureUrlHasTrailingSlash)
                .collect(Collectors.toList());
    }

    public String ensureUrlHasTrailingSlash(String url) {
        return !url.endsWith("/") ? url + "/" : url;
    }
}
