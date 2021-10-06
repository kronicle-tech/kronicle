package tech.kronicle.service.repofinders.github;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.github.client.GitHubClient;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubRepoFinderTest {

    private static final GitHubRepoFinderPersonalAccessTokenConfig USER_1 = new GitHubRepoFinderPersonalAccessTokenConfig("test-user-1", "test-personal-access-token-1");
    private static final GitHubRepoFinderPersonalAccessTokenConfig USER_2 = new GitHubRepoFinderPersonalAccessTokenConfig("test-user-2", "test-personal-access-token-2");
    private static final GitHubRepoFinderPersonalAccessTokenConfig USER_3 = new GitHubRepoFinderPersonalAccessTokenConfig("test-user-3", "test-personal-access-token-3");
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private GitHubRepoFinder underTest;
    @Mock
    private GitHubClient mockClient;

    @Test
    public void getApiReposShouldReturnAnEmptyListWhenUsersListInConfigIsNull() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, TIMEOUT);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getApiReposShouldReturnAnEmptyListWhenUsersListInConfigIsEmpty() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(List.of(), TIMEOUT);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getApiReposShouldCallClientForAUserInConfig() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(List.of(USER_1), TIMEOUT);
        List<ApiRepo> apiRepos1 = List.of(new ApiRepo("https://example.com/repo-1-a.git", true), new ApiRepo("https://example.com/repo-1-b.git", false));
        when(mockClient.getRepos(USER_1)).thenReturn(apiRepos1);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEqualTo(apiRepos1);
    }

    @Test
    public void getApiReposShouldCallClientForMultipleUsersInConfig() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(List.of(USER_1, USER_2, USER_3), TIMEOUT);
        List<ApiRepo> apiRepos1 = List.of(new ApiRepo("https://example.com/repo-1-a.git", true), new ApiRepo("https://example.com/repo-1-b.git", false));
        List<ApiRepo> apiRepos2 = List.of(new ApiRepo("https://example.com/repo-2-a.git", true), new ApiRepo("https://example.com/repo-2-b.git", false));
        List<ApiRepo> apiRepos3 = List.of(new ApiRepo("https://example.com/repo-3-a.git", true), new ApiRepo("https://example.com/repo-3-b.git", false));
        when(mockClient.getRepos(USER_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(USER_2)).thenReturn(apiRepos2);
        when(mockClient.getRepos(USER_3)).thenReturn(apiRepos3);
        underTest = new GitHubRepoFinder(config, mockClient);
        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        List<ApiRepo> allApiRepos = Stream.of(apiRepos1, apiRepos2, apiRepos3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allApiRepos);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsAnEmptyList() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(List.of(USER_1), TIMEOUT);
        List<ApiRepo> apiRepos1 = List.of();
        when(mockClient.getRepos(USER_1)).thenReturn(apiRepos1);
        underTest = new GitHubRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEqualTo(apiRepos1);
    }

    @Test
    public void getApiReposShouldDeduplicateIdenticalApiRepos() {
        // Given
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(List.of(USER_1, USER_2), TIMEOUT);
        ApiRepo apiRepo1 = new ApiRepo("https://example.com/repo-1.git", true);
        ApiRepo apiRepo2 = new ApiRepo("https://example.com/repo-2.git", false);
        ApiRepo apiRepo3 = new ApiRepo("https://example.com/repo-3.git", false);
        List<ApiRepo> apiRepos1 = List.of(apiRepo1, apiRepo2);
        List<ApiRepo> apiRepos2 = List.of(apiRepo2, apiRepo3);
        when(mockClient.getRepos(USER_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(USER_2)).thenReturn(apiRepos2);
        underTest = new GitHubRepoFinder(config, mockClient);
        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).hasSize(3);
        assertThat(returnValue).containsExactly(apiRepo1, apiRepo2, apiRepo3);
    }
}
