package tech.kronicle.service.repofinders.bitbucketserver;

import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.bitbucketserver.client.BitbucketServerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitbucketServerRepoFinderTest {

    private BitbucketServerRepoFinder underTest;
    @Mock
    private BitbucketServerClient mockClient;

    @BeforeEach
    public void beforeEach() {
        underTest = new BitbucketServerRepoFinder(mockClient);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnApiRepos() {
        // Given
        List<ApiRepo> apiRepos = List.of(new ApiRepo("https://example.com/repo-1.git", true), new ApiRepo("https://example.com/repo-2.git", false));
        when(mockClient.getNormalRepos()).thenReturn(apiRepos);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isSameAs(apiRepos);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsAnEmptyList() {
        // Given
        List<ApiRepo> apiRepos = List.of();
        when(mockClient.getNormalRepos()).thenReturn(apiRepos);

        // When
        List<ApiRepo> returnValue = underTest.findApiRepos();

        // Then
        assertThat(returnValue).isSameAs(apiRepos);
    }
}
