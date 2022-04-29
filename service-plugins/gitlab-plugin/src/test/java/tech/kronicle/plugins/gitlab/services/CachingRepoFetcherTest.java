package tech.kronicle.plugins.gitlab.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.Repo;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepo;
import static tech.kronicle.plugins.gitlab.testutils.GitLabJobUtils.createGitLabJobs;
import static tech.kronicle.plugins.gitlab.testutils.RepoUtils.createRepo;
import static tech.kronicle.plugins.gitlab.testutils.RepoUtils.createRepoState;

@ExtendWith(MockitoExtension.class)
public class CachingRepoFetcherTest {

    private static final Clock CLOCK = Clock.fixed(
            LocalDateTime.of(2001, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );
    private static final LocalDateTime NOW = LocalDateTime.now(CLOCK);

    @Mock
    private RepoFetcher mockFetcher;
    @Mock
    private RepoMapper mockMapper;
    @Mock
    private ReposCacheLoader mockLoader;

    @Test
    public void getReposShouldReturnAnEmptyListWhenLoaderReturnsAnEmptyList() {
        // Given
        when(mockLoader.load(CachingRepoFetcher.REPOS_CACHE_KEY)).thenReturn(List.of());
        CachingRepoFetcher underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getReposShouldEnrichTheReposWhenLoaderReturnsMultipleRepos() {
        // Given
        int repoCount = 3;
        List<EnrichedGitLabRepo> enrichedGitLabRepos = IntStream.rangeClosed(1, repoCount)
                .mapToObj(EnrichedGitLabRepoUtils::createEnrichedGitLabRepo)
                .collect(toUnmodifiableList());
        when(mockLoader.load(CachingRepoFetcher.REPOS_CACHE_KEY)).thenReturn(enrichedGitLabRepos);
        List<Repo> expectedRepos = IntStream.range(0, repoCount)
                .mapToObj(repoIndex -> {
                    int repoNumber = repoIndex + 1;
                    EnrichedGitLabRepo enrichedGitLabRepo = enrichedGitLabRepos.get(repoIndex);
                    Repo repo = createRepo(repoNumber).withState(null);
                    List<GitLabJob> jobs = createGitLabJobs(repoNumber);
                    ComponentState repoState = createRepoState(repoNumber);
                    when(mockMapper.mapRepo(enrichedGitLabRepo)).thenReturn(repo);
                    when(mockFetcher.getRepoState(enrichedGitLabRepo)).thenReturn(jobs);
                    when(mockMapper.mapState(jobs, NOW)).thenReturn(repoState);
                    return repo.withState(repoState);
                })
                .collect(toUnmodifiableList());
        CachingRepoFetcher underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEqualTo(expectedRepos);
    }

    private CachingRepoFetcher createUnderTest() {
        return new CachingRepoFetcher(
                mockFetcher,
                mockMapper,
                CLOCK,
                mockLoader,
                new GitLabConfig(
                        null,
                        null,
                        null,
                        null,
                        Duration.ofMinutes(1)
                )
        );
    }
}
