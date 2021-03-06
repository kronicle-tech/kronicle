package tech.kronicle.plugins.bitbucketserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.bitbucketserver.client.BitbucketServerClient;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitbucketServerRepoFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

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
                Repo.builder()
                        .url("https://example.com/repo-1.git")
                        .hasComponentMetadataFile(true)
                        .build(),
                Repo.builder()
                        .url("https://example.com/repo-2.git")
                        .hasComponentMetadataFile(false)
                        .build()
        );
        when(mockClient.getNormalRepos()).thenReturn(repos);

        // When
        Output<List<Repo>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(repos, CACHE_TTL));
    }

    @Test
    public void findShouldCallClientAndReturnAnEmptyListOfApiReposWhenClientReturnsAnEmptyList() {
        // Given
        List<Repo> repos = List.of();
        when(mockClient.getNormalRepos()).thenReturn(repos);

        // When
        Output<List<Repo>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(repos, CACHE_TTL));
    }
}
