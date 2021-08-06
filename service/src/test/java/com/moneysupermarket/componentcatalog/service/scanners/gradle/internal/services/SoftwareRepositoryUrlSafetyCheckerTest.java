package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.config.GradleConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.GOOGLE;
import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.GRADLE_PLUGIN_PORTAL;
import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.JCENTER;
import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.MAVEN_CENTRAL;
import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls.SPRING_PLUGINS_RELEASE;
import static org.assertj.core.api.Assertions.assertThat;

public class SoftwareRepositoryUrlSafetyCheckerTest {

    private static final String CUSTOM_SAFE_REPO_URL = "https://repo.example.com/test-1/";
    private static final String UNSAFE_REPO_URL = "https://repo.example.com/test-2/";

    private final SoftwareRepositoryUrlSafetyChecker underTest = new SoftwareRepositoryUrlSafetyChecker(new GradleConfig(List.of(CUSTOM_SAFE_REPO_URL), null));
    
    @Test
    public void isSoftwareRepositorySafeShouldMatchSafeUrlWithTrailingSlash() {
        // Given
        String url = "https://repo.spring.io/plugins-release/";

        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(url);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void isSoftwareRepositorySafeShouldMatchSafeUrlWithoutTrailingSlash() {
        // Given
        String url = "https://repo.spring.io/plugins-release";

        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(url);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void isSoftwareRepositorySafeShouldNotMatchUnsafeUrl() {
        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(UNSAFE_REPO_URL);

        // Then
        assertThat(returnValue).isFalse();
    }

    @Test
    public void isSoftwareRepositorySafeShouldMatchCustomSafeUrl() {
        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(CUSTOM_SAFE_REPO_URL);

        // Then
        assertThat(returnValue).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {GRADLE_PLUGIN_PORTAL, JCENTER, MAVEN_CENTRAL, SPRING_PLUGINS_RELEASE})
    public void isSoftwareRepositorySafeShouldReturnTrueForSafeUrls(String url) {
        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(url);

        // Then
        assertThat(returnValue).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {GOOGLE})
    public void isSoftwareRepositorySafeShouldReturnFalseForUnsafeUrls(String url) {
        // When
        Boolean returnValue = underTest.isSoftwareRepositoryUrlSafe(url);

        // Then
        assertThat(returnValue).isFalse();
    }
}
