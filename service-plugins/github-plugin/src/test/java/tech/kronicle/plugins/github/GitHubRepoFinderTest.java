package tech.kronicle.plugins.github;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.github.client.GitHubClient;
import tech.kronicle.plugins.github.config.GitHubAccessTokenConfig;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.plugins.github.config.GitHubOrganizationConfig;
import tech.kronicle.plugins.github.config.GitHubUserConfig;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubRepoFinderTest {

    private static final GitHubAccessTokenConfig ACCESS_TOKEN_1 = createAccessToken(1);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_2 = createAccessToken(2);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_3 = createAccessToken(3);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_4 = createAccessToken(4);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_5 = createAccessToken(5);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_6 = createAccessToken(6);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_7 = createAccessToken(7);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_8 = createAccessToken(8);
    private static final GitHubAccessTokenConfig ACCESS_TOKEN_9 = createAccessToken(9);
    private static final GitHubUserConfig USER_1 = new GitHubUserConfig("test-user-1", ACCESS_TOKEN_4);
    private static final GitHubUserConfig USER_2 = new GitHubUserConfig("test-user-2", ACCESS_TOKEN_5);
    private static final GitHubUserConfig USER_3 = new GitHubUserConfig("test-user-3", ACCESS_TOKEN_6);
    private static final GitHubOrganizationConfig ORGANIZATION_1 = new GitHubOrganizationConfig("test-organization-1",ACCESS_TOKEN_7);
    private static final GitHubOrganizationConfig ORGANIZATION_2 = new GitHubOrganizationConfig("test-organization-2", ACCESS_TOKEN_8);
    private static final GitHubOrganizationConfig ORGANIZATION_3 = new GitHubOrganizationConfig("test-organization-3", ACCESS_TOKEN_9);
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private GitHubRepoFinder underTest;
    @Mock
    private GitHubClient mockClient;

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        underTest = new GitHubRepoFinder(null, null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Find repositories hosted by GitHub.  ");
    }

    @Test
    public void findShouldReturnAnEmptyListWhenConfigListsAreNull() {
        // Given
        GitHubConfig config = new GitHubConfig(null, null, null, null, TIMEOUT);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldReturnAnEmptyListWhenConfigListsAreEmpty() {
        // Given
        GitHubConfig config = new GitHubConfig(null, List.of(), List.of(), List.of(), TIMEOUT);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldCallClientForAnItemInEachConfigList() {
        // Given
        GitHubConfig config = new GitHubConfig(null, List.of(ACCESS_TOKEN_1), List.of(USER_1), List.of(ORGANIZATION_1), TIMEOUT);
        List<Repo> repos1 = createApiRepos(1);
        List<Repo> repos2 = createApiRepos(2);
        List<Repo> repos3 = createApiRepos(3);
        when(mockClient.getRepos(ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(USER_1)).thenReturn(repos2);
        when(mockClient.getRepos(ORGANIZATION_1)).thenReturn(repos3);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        List<Repo> allRepos = Stream.of(repos1, repos2, repos3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allRepos);
    }

    @Test
    public void findShouldCallClientForItemsInEachConfigList() {
        // Given
        GitHubConfig config = new GitHubConfig(null, 
                List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2, ACCESS_TOKEN_3),
                List.of(USER_1, USER_2, USER_3),
                List.of(ORGANIZATION_1, ORGANIZATION_2, ORGANIZATION_3),
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
        when(mockClient.getRepos(ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(ACCESS_TOKEN_3)).thenReturn(repos3);
        when(mockClient.getRepos(USER_1)).thenReturn(repos4);
        when(mockClient.getRepos(USER_2)).thenReturn(repos5);
        when(mockClient.getRepos(USER_3)).thenReturn(repos6);
        when(mockClient.getRepos(ORGANIZATION_1)).thenReturn(repos7);
        when(mockClient.getRepos(ORGANIZATION_2)).thenReturn(repos8);
        when(mockClient.getRepos(ORGANIZATION_3)).thenReturn(repos9);
        underTest = new GitHubRepoFinder(config, mockClient);
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
        GitHubConfig config = new GitHubConfig(null, List.of(ACCESS_TOKEN_1), List.of(USER_1), List.of(ORGANIZATION_1), TIMEOUT);
        when(mockClient.getRepos(ACCESS_TOKEN_1)).thenReturn(List.of());
        when(mockClient.getRepos(USER_1)).thenReturn(List.of());
        when(mockClient.getRepos(ORGANIZATION_1)).thenReturn(List.of());
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldDeduplicateIdenticalApiRepos() {
        // Given
        GitHubConfig config = new GitHubConfig(null, 
                List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2),
                List.of(USER_1, USER_2),
                List.of(ORGANIZATION_1, ORGANIZATION_2),
                TIMEOUT);
        Repo repo1 = new Repo("https://example.com/repo-1.git", true);
        Repo repo2 = new Repo("https://example.com/repo-2.git", false);
        Repo repo3 = new Repo("https://example.com/repo-3.git", false);
        List<Repo> repos1 = List.of(repo1, repo2);
        List<Repo> repos2 = List.of(repo2, repo3);
        when(mockClient.getRepos(ACCESS_TOKEN_1)).thenReturn(repos1);
        when(mockClient.getRepos(ACCESS_TOKEN_2)).thenReturn(repos2);
        when(mockClient.getRepos(USER_1)).thenReturn(repos1);
        when(mockClient.getRepos(USER_2)).thenReturn(repos2);
        when(mockClient.getRepos(ORGANIZATION_1)).thenReturn(repos1);
        when(mockClient.getRepos(ORGANIZATION_2)).thenReturn(repos2);
        underTest = new GitHubRepoFinder(config, mockClient);
        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).hasSize(3);
        assertThat(returnValue).containsExactly(repo1, repo2, repo3);
    }

    private static GitHubAccessTokenConfig createAccessToken(int number) {
        return new GitHubAccessTokenConfig("test-personal-access-token-username-" + number, "test-personal-access-token-" + number);
    }

    private static List<Repo> createApiRepos(int number) {
        return List.of(new Repo("https://example.com/repo-" + number + "-a.git", true), new Repo("https://example.com/repo-" + number + "-b.git", false));
    }
}
