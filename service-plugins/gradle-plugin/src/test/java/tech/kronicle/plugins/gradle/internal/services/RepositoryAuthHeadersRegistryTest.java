package tech.kronicle.plugins.gradle.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.GradleCustomRepository;
import tech.kronicle.plugins.gradle.config.HttpHeaderConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryAuthHeadersRegistryTest {

    @Test
    void getSoftwareRepositoryAuthHeadersWhenCustomRepositoriesListIsNullShouldReturnNull() {
        // Given
        GradleConfig config = new GradleConfig(null, null, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-2/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    void getSoftwareRepositoryAuthHeadersWhenCustomRepositoriesListIsEmptyShouldReturnNull() {
        // Given
        GradleConfig config = new GradleConfig(List.of(), null, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-2/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    void getSoftwareRepositoryAuthHeadersWhenUrlDoesNotMatchACustomSoftwareRepositoryShouldReturnNull() {
        // Given
        List<GradleCustomRepository> customRepositories = List.of(
                new GradleCustomRepository("testCustomRepository1", "https://example.com/repo-1", List.of(
                        new HttpHeaderConfig("test-header-1", "test-value-1")
                ))
        );
        GradleConfig config = new GradleConfig(null, customRepositories, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-2/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    void getSoftwareRepositoryAuthHeadersWhenUrlMatchesACustomSoftwareRepositoryShouldReturnTheAssociatedAuthHeaders() {
        // Given
        List<HttpHeaderConfig> httpHeaders = List.of(
                new HttpHeaderConfig("test-header-1", "test-value-1")
        );
        List<GradleCustomRepository> customRepositories = List.of(
                new GradleCustomRepository("testCustomRepository1", "https://example.com/repo-1", httpHeaders)
        );
        GradleConfig config = new GradleConfig(null, customRepositories, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-1/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).containsExactlyElementsOf(httpHeaders);
    }

    @Test
    void getSoftwareRepositoryAuthHeadersWhenCustomRepositoryUrlIsMissingATrailingSlashAndDoesMatchWhenAddingATrailingSlashShouldReturnTheAssociatedAuthHeaders() {
        // Given
        List<HttpHeaderConfig> httpHeaders = List.of(
                new HttpHeaderConfig("test-header-1", "test-value-1")
        );
        List<GradleCustomRepository> customRepositories = List.of(
                new GradleCustomRepository("testCustomRepository1", "https://example.com/repo-1", httpHeaders)
        );
        GradleConfig config = new GradleConfig(null, customRepositories, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-1/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).containsExactlyElementsOf(httpHeaders);
    }


    @Test
    void getSoftwareRepositoryAuthHeadersWhenCustomRepositoryUrlIsMissingATrailingSlashAndDoesNotMatchWhenAddingATrailingSlashShouldReturnNull() {
        // Given
        List<HttpHeaderConfig> httpHeaders = List.of(
                new HttpHeaderConfig("test-header-1", "test-value-1")
        );
        List<GradleCustomRepository> customRepositories = List.of(
                new GradleCustomRepository("testCustomRepository1", "https://example.com/repo-1", httpHeaders)
        );
        GradleConfig config = new GradleConfig(null, customRepositories, null, null, null, null);
        RepositoryAuthHeadersRegistry underTest = new RepositoryAuthHeadersRegistry(config);

        // When
        List<HttpHeaderConfig> returnValue = underTest.getRepositoryAuthHeaders("https://example.com/repo-11/test.group.id/test-artifact-id/test-artifact-id:test-version.pom");

        // Then
        assertThat(returnValue).isNull();
    }
}