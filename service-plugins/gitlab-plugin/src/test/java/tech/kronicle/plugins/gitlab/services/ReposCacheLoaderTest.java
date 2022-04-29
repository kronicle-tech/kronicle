package tech.kronicle.plugins.gitlab.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepos;

@ExtendWith(MockitoExtension.class)
public class ReposCacheLoaderTest {

    @Mock
    private RepoFetcher mockFetcher;

    @Test
    public void findShouldUseCachingRepoFetcherToFindRepos() {
        // Given
        List<EnrichedGitLabRepo> repos = createEnrichedGitLabRepos();
        when(mockFetcher.getRepos()).thenReturn(repos);
        ReposCacheLoader underTest = new ReposCacheLoader(mockFetcher);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.load(new Object());

        // Then
        assertThat(returnValue).isEqualTo(repos);
    }
}
