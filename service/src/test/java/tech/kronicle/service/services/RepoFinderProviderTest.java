package tech.kronicle.service.services;

import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.RepoFinder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class RepoFinderProviderTest {

    private RepoFinderProvider underTest;

    @Test
    public void getRepoProvidersShouldProvideACopyOfTheListThatWasPassedToConstructor() {
        // Given
        List<RepoFinder> repoFinders = List.of(new TestRepoFinder(), new TestRepoFinder());
        underTest = new RepoFinderProvider(repoFinders);

        // When
        List<RepoFinder> returnValue = underTest.getRepoFinders();

        // Then
        assertThat(returnValue).containsExactlyElementsOf(repoFinders);
    }

    @Test
    public void getRepoProvidersShouldReturnAnUnmodifiableList() {
        // Given
        List<RepoFinder> repoFinders = new ArrayList<>();
        repoFinders.add(new TestRepoFinder());
        underTest = new RepoFinderProvider(repoFinders);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepoFinders().add(new TestRepoFinder()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    private static class TestRepoFinder extends RepoFinder {

        @Override
        public List<ApiRepo> getApiRepos() {
            return List.of();
        }
    }
}
