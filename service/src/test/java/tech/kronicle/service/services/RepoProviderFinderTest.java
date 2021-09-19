package tech.kronicle.service.services;

import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.RepoProvider;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class RepoProviderFinderTest {

    private RepoProviderFinder underTest;

    @Test
    public void getRepoProvidersShouldProvideACopyOfTheListThatWasPassedToConstructor() {
        // Given
        List<RepoProvider> repoProviders = List.of(new TestRepoProvider(), new TestRepoProvider());
        underTest = new RepoProviderFinder(repoProviders);

        // When
        List<RepoProvider> returnValue = underTest.getRepoProviders();

        // Then
        assertThat(returnValue).containsExactlyElementsOf(repoProviders);
    }

    @Test
    public void getRepoProvidersShouldReturnAnUnmodifiableList() {
        // Given
        List<RepoProvider> repoProviders = new ArrayList<>();
        repoProviders.add(new TestRepoProvider());
        underTest = new RepoProviderFinder(repoProviders);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepoProviders().add(new TestRepoProvider()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    private static class TestRepoProvider extends RepoProvider {

        @Override
        public List<ApiRepo> getApiRepos() {
            return List.of();
        }
    }
}
