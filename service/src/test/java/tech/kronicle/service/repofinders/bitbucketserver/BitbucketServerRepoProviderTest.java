package tech.kronicle.service.repoproviders.bitbucketserver;

import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repoproviders.bitbucketserver.client.BitbucketServerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitbucketServerRepoProviderTest {

    private BitbucketServerRepoProvider underTest;
    @Mock
    private BitbucketServerClient mockClient;

    @BeforeEach
    public void beforeEach() {
        underTest = new BitbucketServerRepoProvider(mockClient);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnApiRepos() {
        // Given
        List<ApiRepo> apiRepos = List.of(new ApiRepo("https://example.com/repo-1.git", true), new ApiRepo("https://example.com/repo-2.git", false));
        when(mockClient.getNormalRepos()).thenReturn(apiRepos);

        // When
        List<ApiRepo> returnValue = underTest.getApiRepos();

        // Then
        assertThat(returnValue).isSameAs(apiRepos);
    }

    @Test
    public void getApiReposShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsAnEmptyList() {
        // Given
        List<ApiRepo> apiRepos = List.of();
        when(mockClient.getNormalRepos()).thenReturn(apiRepos);

        // When
        List<ApiRepo> returnValue = underTest.getApiRepos();

        // Then
        assertThat(returnValue).isSameAs(apiRepos);
    }
}
