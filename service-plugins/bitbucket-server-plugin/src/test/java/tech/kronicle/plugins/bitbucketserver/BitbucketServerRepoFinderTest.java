package tech.kronicle.plugins.bitbucketserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;

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
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Find repositories hosted by Bitbucket Server.  ");
    }

    @Test
    public void findShouldCallClientAndReturnApiRepos() {
        // Given
        List<Repo> repos = List.of(
                new Repo("https://example.com/repo-1.git", null, true, null),
                new Repo("https://example.com/repo-2.git", null, false, null)
        );
        when(mockClient.getNormalRepos()).thenReturn(repos);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isSameAs(repos);
    }

    @Test
    public void findShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsAnEmptyList() {
        // Given
        List<Repo> repos = List.of();
        when(mockClient.getNormalRepos()).thenReturn(repos);

        // When
        List<Repo> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isSameAs(repos);
    }
}
