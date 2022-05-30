package tech.kronicle.plugins.gitlab.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.gitlab.GitLabRepoFinder;
import tech.kronicle.plugins.gitlab.client.GitLabClient;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.sdk.models.Repo;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepo;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepos;
import static tech.kronicle.plugins.gitlab.testutils.GitLabJobUtils.createGitLabJobs;
import static tech.kronicle.plugins.gitlab.testutils.RepoUtils.createRepos;

@ExtendWith(MockitoExtension.class)
public class RepoFetcherTest {

    private static final String BASE_URL = "https://example.com/base-url";
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_1 = createAccessToken(1);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_2 = createAccessToken(2);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_3 = createAccessToken(3);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_4 = createAccessToken(4);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_5 = createAccessToken(5);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_6 = createAccessToken(6);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_7 = createAccessToken(7);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_8 = createAccessToken(8);
    private static final GitLabAccessTokenConfig ACCESS_TOKEN_9 = createAccessToken(9);
    private static final GitLabUserConfig USER_1 = new GitLabUserConfig("test-user-1", ACCESS_TOKEN_4);
    private static final GitLabUserConfig USER_2 = new GitLabUserConfig("test-user-2", ACCESS_TOKEN_5);
    private static final GitLabUserConfig USER_3 = new GitLabUserConfig("test-user-3", ACCESS_TOKEN_6);
    private static final GitLabGroupConfig GROUP_1 = new GitLabGroupConfig("test-group-1",ACCESS_TOKEN_7);
    private static final GitLabGroupConfig GROUP_2 = new GitLabGroupConfig("test-group-2", ACCESS_TOKEN_8);
    private static final GitLabGroupConfig GROUP_3 = new GitLabGroupConfig("test-group-3", ACCESS_TOKEN_9);
    private static final int PAGE_SIZE = 5;
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private RepoFetcher underTest;
    @Mock
    private GitLabClient mockClient;

    @Test
    public void getReposShouldReturnAnEmptyListWhenHostsListIsNull() {
        // Given
        GitLabConfig config = createConfig(null);
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getReposShouldReturnAnEmptyListWhenHostsListIsEmpty() {
        // Given
        GitLabConfig config = createConfig(List.of());
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getReposShouldCallClientForAnItemInEachListOfAHost() {
        // Given
        GitLabConfig config = createConfig(List.of(new GitLabHostConfig(BASE_URL,
                List.of(ACCESS_TOKEN_1),
                List.of(USER_1),
                List.of(GROUP_1))));
        List<EnrichedGitLabRepo> repos1 = createEnrichedGitLabRepos(1);
        List<EnrichedGitLabRepo> repos2 = createEnrichedGitLabRepos(2);
        List<EnrichedGitLabRepo> repos3 = createEnrichedGitLabRepos(3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos3);
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        List<EnrichedGitLabRepo> allRepos = Stream.of(repos1, repos2, repos3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allRepos);
    }

    @Test
    public void getReposShouldCallClientForItemsInEachConfigListOfAHost() {
        // Given
        GitLabConfig config = createConfig(List.of(new GitLabHostConfig(BASE_URL,
                List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2, ACCESS_TOKEN_3),
                List.of(USER_1, USER_2, USER_3),
                List.of(GROUP_1, GROUP_2, GROUP_3))));
        List<EnrichedGitLabRepo> repos1 = createEnrichedGitLabRepos(1);
        List<EnrichedGitLabRepo> repos2 = createEnrichedGitLabRepos(2);
        List<EnrichedGitLabRepo> repos3 = createEnrichedGitLabRepos(3);
        List<EnrichedGitLabRepo> repos4 = createEnrichedGitLabRepos(4);
        List<EnrichedGitLabRepo> repos5 = createEnrichedGitLabRepos(5);
        List<EnrichedGitLabRepo> repos6 = createEnrichedGitLabRepos(6);
        List<EnrichedGitLabRepo> repos7 = createEnrichedGitLabRepos(7);
        List<EnrichedGitLabRepo> repos8 = createEnrichedGitLabRepos(8);
        List<EnrichedGitLabRepo> repos9 = createEnrichedGitLabRepos(9);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_3)).thenReturn(repos3);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos4);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(repos5);
        when(mockClient.getRepos(BASE_URL, USER_3)).thenReturn(repos6);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos7);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(repos8);
        when(mockClient.getRepos(BASE_URL, GROUP_3)).thenReturn(repos9);
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        List<EnrichedGitLabRepo> allRepos = Stream.of(repos1, repos2, repos3, repos4, repos5, repos6, repos7, repos8, repos9)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allRepos);
    }

    @Test
    public void getReposShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsEmptyLists() {
        // Given
        GitLabConfig config = createConfig(List.of(new GitLabHostConfig(BASE_URL,
                List.of(ACCESS_TOKEN_1),
                List.of(USER_1),
                List.of(GROUP_1))));
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(List.of());
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(List.of());
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(List.of());
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getReposShouldDeduplicateIdenticalApiRepos() {
        // Given
        GitLabConfig config = createConfig(List.of(new GitLabHostConfig(BASE_URL,
                List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2),
                List.of(USER_1, USER_2),
                List.of(GROUP_1, GROUP_2))));
        EnrichedGitLabRepo repo1 = createEnrichedGitLabRepo(1, 1);
        EnrichedGitLabRepo repo2 = createEnrichedGitLabRepo(1, 2);
        EnrichedGitLabRepo repo3 = createEnrichedGitLabRepo(1, 3);
        List<EnrichedGitLabRepo> repos1 = List.of(repo1, repo2);
        List<EnrichedGitLabRepo> repos2 = List.of(repo2, repo3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(repos2);
        underTest = new RepoFetcher(config, mockClient);

        // When
        List<EnrichedGitLabRepo> returnValue = underTest.getRepos();

        // Then
        assertThat(returnValue).containsExactly(repo1, repo2, repo3);
    }

    @Test
    public void getRepoJobsShouldUseTheClientToGetRepoJobsForARepo() {
        // Given
        List<GitLabJob> jobs = createGitLabJobs();
        EnrichedGitLabRepo repo = createEnrichedGitLabRepo(1, 1);
        when(mockClient.getJobs(repo)).thenReturn(jobs);
        underTest = new RepoFetcher(null, mockClient);

        // When
        List<GitLabJob> returnValue = underTest.getRepoJobs(repo);

        // Then
        assertThat(returnValue).isEqualTo(jobs);
    }

    private GitLabConfig createConfig(List<GitLabHostConfig> hosts) {
        return new GitLabConfig(
                hosts,
                PAGE_SIZE,
                null,
                TIMEOUT,
                null
        );
    }

    private static GitLabAccessTokenConfig createAccessToken(int accessTokenNumber) {
        return new GitLabAccessTokenConfig("test-access-token-" + accessTokenNumber);
    }
}
