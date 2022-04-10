package tech.kronicle.plugins.gitlab;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.gitlab.client.GitLabClient;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitLabRepoFinderTest {

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

    private GitLabRepoFinder underTest;
    @Mock
    private GitLabClient mockClient;

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        underTest = new GitLabRepoFinder(null, null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Find repositories hosted by GitLab.  ");
    }

    @Test
    public void findShouldReturnAnEmptyListWhenHostsListIsNull() {
        // Given
        GitLabConfig config = new GitLabConfig(null, PAGE_SIZE, TIMEOUT);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldReturnAnEmptyListWhenHostsListIsEmpty() {
        // Given
        GitLabConfig config = new GitLabConfig(List.of(), PAGE_SIZE, TIMEOUT);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldCallClientForAnItemInEachListOfAHost() {
        // Given
        GitLabConfig config = new GitLabConfig(
                List.of(new GitLabHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1),
                        List.of(USER_1),
                        List.of(GROUP_1))),
                PAGE_SIZE,
                TIMEOUT);
        List<Repo> repos1 = createApiRepos(1);
        List<Repo> repos2 = createApiRepos(2);
        List<Repo> repos3 = createApiRepos(3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos3);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        List<Repo> allRepos = Stream.of(repos1, repos2, repos3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allRepos);
    }

    @Test
    public void findShouldCallClientForItemsInEachConfigListOfAHost() {
        // Given
        GitLabConfig config = new GitLabConfig(
                List.of(new GitLabHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2, ACCESS_TOKEN_3),
                        List.of(USER_1, USER_2, USER_3),
                        List.of(GROUP_1, GROUP_2, GROUP_3))),
                PAGE_SIZE,
                TIMEOUT);
        List<Repo> repos1 = createApiRepos(1);
        List<Repo> repos2 = createApiRepos(2);
        List<Repo> repos3 = createApiRepos(3);
        List<Repo> repos4 = createApiRepos(4);
        List<Repo> repos5 = createApiRepos(5);
        List<Repo> repos6 = createApiRepos(6);
        List<Repo> repos7 = createApiRepos(7);
        List<Repo> repos8 = createApiRepos(8);
        List<Repo> repos9 = createApiRepos(9);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_3)).thenReturn(repos3);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos4);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(repos5);
        when(mockClient.getRepos(BASE_URL, USER_3)).thenReturn(repos6);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos7);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(repos8);
        when(mockClient.getRepos(BASE_URL, GROUP_3)).thenReturn(repos9);
        underTest = new GitLabRepoFinder(config, mockClient);
        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        List<Repo> allRepos = Stream.of(repos1, repos2, repos3, repos4, repos5, repos6, repos7, repos8, repos9)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allRepos);
    }

    @Test
    public void findShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsEmptyLists() {
        // Given
        GitLabConfig config = new GitLabConfig(
                List.of(new GitLabHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1),
                        List.of(USER_1),
                        List.of(GROUP_1))),
                PAGE_SIZE,
                TIMEOUT);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(List.of());
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(List.of());
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(List.of());
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldDeduplicateIdenticalApiRepos() {
        // Given
        GitLabConfig config = new GitLabConfig(
                List.of(new GitLabHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2),
                        List.of(USER_1, USER_2),
                        List.of(GROUP_1, GROUP_2))),
                PAGE_SIZE,
                TIMEOUT);
        Repo repo1 = Repo.builder()
                .url("https://example.com/repo-1.git")
                .hasComponentMetadataFile(true)
                .build();
        Repo repo2 = Repo.builder()
                .url("https://example.com/repo-2.git")
                .hasComponentMetadataFile(false)
                .build();
        Repo repo3 = Repo.builder()
                .url("https://example.com/repo-3.git")
                .hasComponentMetadataFile(false)
                .build();
        List<Repo> repos1 = List.of(repo1, repo2);
        List<Repo> repos2 = List.of(repo2, repo3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(repos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(repos1);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(repos2);
        underTest = new GitLabRepoFinder(config, mockClient);
        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).containsExactly(repo1, repo2, repo3);
    }

    private static GitLabAccessTokenConfig createAccessToken(int number) {
        return new GitLabAccessTokenConfig("test-access-token-" + number);
    }

    private static List<Repo> createApiRepos(int number) {
        return List.of(
                Repo.builder()
                        .url("https://example.com/repo-" + number + "-a.git")
                        .hasComponentMetadataFile(true)
                        .build(),
                Repo.builder()
                        .url("https://example.com/repo-" + number + "-b.git")
                        .hasComponentMetadataFile(false)
                        .build()
        );
    }
}
