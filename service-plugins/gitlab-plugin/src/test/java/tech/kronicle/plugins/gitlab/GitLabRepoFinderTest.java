package tech.kronicle.plugins.gitlab;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.gitlab.services.CachingRepoFetcher;
import tech.kronicle.sdk.models.Repo;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.gitlab.testutils.RepoUtils.createRepos;

@ExtendWith(MockitoExtension.class)
public class GitLabRepoFinderTest {

    private static final Duration CACHE_TTL = Duration.ZERO;

    @Mock
    private CachingRepoFetcher mockFetcher;

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        GitLabRepoFinder underTest = new GitLabRepoFinder(null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Find repositories hosted by GitLab.  ");
    }

    @Test
    public void findShouldUseCachingRepoFetcherToFindRepos() {
        // Given
        List<Repo> repos = createRepos();
        when(mockFetcher.getRepos()).thenReturn(repos);
        GitLabRepoFinder underTest = new GitLabRepoFinder(mockFetcher);

        // When
        Output<List<Repo>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(repos, CACHE_TTL));
    }
}
