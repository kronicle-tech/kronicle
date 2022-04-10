package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.service.config.RepoFindersConfig;
import tech.kronicle.service.config.RepoFindersIgnoredRepoConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoFilterServiceTest {

    @Test
    public void keepRepoShouldReturnTrueIfIgnoreReposListIsNull() {
        // Given
        RepoFindersConfig config = new RepoFindersConfig(null);
        RepoFilterService underTest = new RepoFilterService(config);

        // When
        boolean returnValue = underTest.keepRepo(new Repo("https://example.com/test-repo", true));

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void keepRepoShouldReturnTrueIfIgnoreReposConfigListIsEmpty() {
        // Given
        RepoFindersConfig config = new RepoFindersConfig(List.of());
        RepoFilterService underTest = new RepoFilterService(config);

        // When
        boolean returnValue = underTest.keepRepo(new Repo("https://example.com/test-repo", true));

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void keepRepoShouldReturnTrueIfUrlOfRepoIsNotInIgnoreReposList() {
        // Given
        RepoFindersConfig config = new RepoFindersConfig(List.of(
                new RepoFindersIgnoredRepoConfig("https://example.com/test-repo-1"),
                new RepoFindersIgnoredRepoConfig("https://example.com/test-repo-3")));
        RepoFilterService underTest = new RepoFilterService(config);

        // When
        boolean returnValue = underTest.keepRepo(new Repo("https://example.com/test-repo-2", true));

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void keepRepoShouldReturnFalseIfUrlOfRepoIsInIgnoreReposList() {
        // Given
        RepoFindersConfig config = new RepoFindersConfig(List.of(
                new RepoFindersIgnoredRepoConfig("https://example.com/test-repo-1"),
                new RepoFindersIgnoredRepoConfig("https://example.com/test-repo-2")));
        RepoFilterService underTest = new RepoFilterService(config);

        // When
        boolean returnValue = underTest.keepRepo(new Repo("https://example.com/test-repo-2", true));

        // Then
        assertThat(returnValue).isFalse();
    }
}
