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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private static final int REPO_COUNT = 3;

    @Mock
    private RepoFetcher mockFetcher;
    @Mock
    private RepoMapper mockMapper;

    @Test
    public void getReposShouldReturnAnEmptyListWhenLoaderReturnsAnEmptyList() {
        // Given
        when(mockFetcher.getRepos()).thenReturn(List.of());
        CachingRepoFetcher underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getReposShouldEnrichTheReposWhenLoaderReturnsMultipleRepos() {
        // Given
        List<Repo> enrichedRepos = mockEverything(1, 1);
        CachingRepoFetcher underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEqualTo(enrichedRepos);
    }

    @Test
    public void getReposShouldNotReloadTheReposBeforeTheCacheTtlHasExpired() {
        // Given
        List<Repo> expectedRepos = mockEverything(1, 1);
        CachingRepoFetcher underTest = createUnderTest(Duration.ofMinutes(1));

        // When
        List<Repo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEqualTo(expectedRepos);

        // When
        List<Repo> returnValue2 = underTest.getRepos();

        // Then
        assertThat(returnValue2).isEqualTo(expectedRepos);

        verify(mockFetcher).getRepos();
    }

    private List<Repo> mockEverything(int cacheIteration, int enrichmentIteration) {
        reset(mockFetcher, mockMapper);
        mockCacheLoader(cacheIteration);
        return mockEnrichment(enrichmentIteration);
    }

    private void mockCacheLoader(int iteration) {
        when(mockFetcher.getRepos()).thenReturn(createCachedRepos(iteration));
    }

    private List<Repo> mockEnrichment(int iteration) {
        List<EnrichedGitLabRepo> cachedRepos = createCachedRepos(iteration);
        int offset = getOffset(iteration);
        return IntStream.range(0, REPO_COUNT)
                .mapToObj(repoIndex -> {
                    int repoNumber = offset + repoIndex + 1;
                    EnrichedGitLabRepo cachedRepo = cachedRepos.get(repoIndex);
                    Repo repo = createRepo(repoNumber).withState(null);
                    List<GitLabJob> jobs = createGitLabJobs(repoNumber);
                    ComponentState repoState = createRepoState(repoNumber);
                    when(mockMapper.mapRepo(cachedRepo)).thenReturn(repo);
                    when(mockFetcher.getRepoState(cachedRepo)).thenReturn(jobs);
                    when(mockMapper.mapState(jobs, NOW)).thenReturn(repoState);
                    return repo.withState(repoState);
                })
                .collect(toUnmodifiableList());
    }

    private List<EnrichedGitLabRepo> createCachedRepos(int iteration) {
        int offset = getOffset(iteration);
        return IntStream.rangeClosed(offset + 1, offset + REPO_COUNT)
                .mapToObj(EnrichedGitLabRepoUtils::createEnrichedGitLabRepo)
                .collect(toUnmodifiableList());
    }

    private int getOffset(int iteration) {
        return (iteration - 1) * REPO_COUNT;
    }

    private CachingRepoFetcher createUnderTest() {
        return createUnderTest(Duration.ofMinutes(1));
    }

    private CachingRepoFetcher createUnderTest(Duration reposCacheTtl) {
        return new CachingRepoFetcher(
                mockFetcher,
                mockMapper,
                CLOCK,
                new ReposCacheLoader(mockFetcher),
                new GitLabConfig(
                        null,
                        null,
                        null,
                        null,
                        reposCacheTtl
                )
        );
    }
}
