package tech.kronicle.service.repofinders.gitlab;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.gitlab.client.GitLabClient;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderAccessTokenConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderGroupConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderHostConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderUserConfig;

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
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_1 = createAccessToken(1);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_2 = createAccessToken(2);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_3 = createAccessToken(3);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_4 = createAccessToken(4);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_5 = createAccessToken(5);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_6 = createAccessToken(6);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_7 = createAccessToken(7);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_8 = createAccessToken(8);
    private static final GitLabRepoFinderAccessTokenConfig ACCESS_TOKEN_9 = createAccessToken(9);
    private static final GitLabRepoFinderUserConfig USER_1 = new GitLabRepoFinderUserConfig("test-user-1", ACCESS_TOKEN_4);
    private static final GitLabRepoFinderUserConfig USER_2 = new GitLabRepoFinderUserConfig("test-user-2", ACCESS_TOKEN_5);
    private static final GitLabRepoFinderUserConfig USER_3 = new GitLabRepoFinderUserConfig("test-user-3", ACCESS_TOKEN_6);
    private static final GitLabRepoFinderGroupConfig GROUP_1 = new GitLabRepoFinderGroupConfig("test-group-1",ACCESS_TOKEN_7);
    private static final GitLabRepoFinderGroupConfig GROUP_2 = new GitLabRepoFinderGroupConfig("test-group-2", ACCESS_TOKEN_8);
    private static final GitLabRepoFinderGroupConfig GROUP_3 = new GitLabRepoFinderGroupConfig("test-group-3", ACCESS_TOKEN_9);
    private static final int PAGE_SIZE = 5;
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private GitLabRepoFinder underTest;
    @Mock
    private GitLabClient mockClient;

    @Test
    public void getApiReposShouldReturnAnEmptyListWhenHostsListIsNull() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(null, PAGE_SIZE, TIMEOUT);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getApiReposShouldReturnAnEmptyListWhenHostsListIsEmpty() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(List.of(), PAGE_SIZE, TIMEOUT);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getApiReposShouldCallClientForAnItemInEachListOfAHost() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(
                List.of(new GitLabRepoFinderHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1),
                        List.of(USER_1),
                        List.of(GROUP_1))),
                PAGE_SIZE,
                TIMEOUT);
        List<ApiRepo> apiRepos1 = createApiRepos(1);
        List<ApiRepo> apiRepos2 = createApiRepos(2);
        List<ApiRepo> apiRepos3 = createApiRepos(3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(apiRepos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(apiRepos3);
        underTest = new GitLabRepoFinder(config, mockClient);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        List<ApiRepo> allApiRepos = Stream.of(apiRepos1, apiRepos2, apiRepos3)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allApiRepos);
    }

    @Test
    public void getApiReposShouldCallClientForItemsInEachConfigListOfAHost() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(
                List.of(new GitLabRepoFinderHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2, ACCESS_TOKEN_3),
                        List.of(USER_1, USER_2, USER_3),
                        List.of(GROUP_1, GROUP_2, GROUP_3))),
                PAGE_SIZE,
                TIMEOUT);
        List<ApiRepo> apiRepos1 = createApiRepos(1);
        List<ApiRepo> apiRepos2 = createApiRepos(2);
        List<ApiRepo> apiRepos3 = createApiRepos(3);
        List<ApiRepo> apiRepos4 = createApiRepos(4);
        List<ApiRepo> apiRepos5 = createApiRepos(5);
        List<ApiRepo> apiRepos6 = createApiRepos(6);
        List<ApiRepo> apiRepos7 = createApiRepos(7);
        List<ApiRepo> apiRepos8 = createApiRepos(8);
        List<ApiRepo> apiRepos9 = createApiRepos(9);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(apiRepos2);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_3)).thenReturn(apiRepos3);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(apiRepos4);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(apiRepos5);
        when(mockClient.getRepos(BASE_URL, USER_3)).thenReturn(apiRepos6);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(apiRepos7);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(apiRepos8);
        when(mockClient.getRepos(BASE_URL, GROUP_3)).thenReturn(apiRepos9);
        underTest = new GitLabRepoFinder(config, mockClient);
        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        List<ApiRepo> allApiRepos = Stream.of(apiRepos1, apiRepos2, apiRepos3, apiRepos4, apiRepos5, apiRepos6, apiRepos7, apiRepos8, apiRepos9)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyInAnyOrderElementsOf(allApiRepos);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsEmptyLists() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(
                List.of(new GitLabRepoFinderHostConfig(BASE_URL,
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
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getApiReposShouldDeduplicateIdenticalApiRepos() {
        // Given
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(
                List.of(new GitLabRepoFinderHostConfig(BASE_URL,
                        List.of(ACCESS_TOKEN_1, ACCESS_TOKEN_2),
                        List.of(USER_1, USER_2),
                        List.of(GROUP_1, GROUP_2))),
                PAGE_SIZE,
                TIMEOUT);
        ApiRepo apiRepo1 = new ApiRepo("https://example.com/repo-1.git", true);
        ApiRepo apiRepo2 = new ApiRepo("https://example.com/repo-2.git", false);
        ApiRepo apiRepo3 = new ApiRepo("https://example.com/repo-3.git", false);
        List<ApiRepo> apiRepos1 = List.of(apiRepo1, apiRepo2);
        List<ApiRepo> apiRepos2 = List.of(apiRepo2, apiRepo3);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(BASE_URL, ACCESS_TOKEN_2)).thenReturn(apiRepos2);
        when(mockClient.getRepos(BASE_URL, USER_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(BASE_URL, USER_2)).thenReturn(apiRepos2);
        when(mockClient.getRepos(BASE_URL, GROUP_1)).thenReturn(apiRepos1);
        when(mockClient.getRepos(BASE_URL, GROUP_2)).thenReturn(apiRepos2);
        underTest = new GitLabRepoFinder(config, mockClient);
        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).containsExactly(apiRepo1, apiRepo2, apiRepo3);
    }

    private static GitLabRepoFinderAccessTokenConfig createAccessToken(int number) {
        return new GitLabRepoFinderAccessTokenConfig("test-access-token-" + number);
    }

    private static List<ApiRepo> createApiRepos(int number) {
        return List.of(
                new ApiRepo("https://example.com/repo-" + number + "-a.git", true),
                new ApiRepo("https://example.com/repo-" + number + "-b.git", false));
    }
}
