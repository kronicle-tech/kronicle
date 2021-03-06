package tech.kronicle.plugins.bitbucketserver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerHostConfig;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class BitbucketServerClientTest {

    private static final Duration TEST_DURATION = Duration.ofSeconds(30);

    private BitbucketServerClient underTest;
    private final HttpClient httpClient = createHttpClient();
    private final ObjectMapper objectMapper = createJsonMapper();
    private WireMockServer wireMockServer;

    @BeforeEach
    public void beforeEach() {
        wireMockServer = new BitbucketServerWireMockFactory().create();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getNormalReposShouldReturnAnEmptyListWhenHostsListInConfigIsNull() {
        // Given
        BitbucketServerConfig config = new BitbucketServerConfig(null, TEST_DURATION);
        underTest = new BitbucketServerClient(httpClient, objectMapper, config);

        // When
        List<Repo> returnValue = underTest.getNormalRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getNormalReposShouldReturnAnEmptyListWhenHostsListInConfigIsEmpty() {
        // Given
        BitbucketServerConfig config = new BitbucketServerConfig(List.of(), TEST_DURATION);
        underTest = new BitbucketServerClient(httpClient, objectMapper, config);

        // When
        List<Repo> returnValue = underTest.getNormalRepos();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getNormalReposShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues() {
        // Given
        BitbucketServerConfig config = new BitbucketServerConfig(
                List.of(
                    new BitbucketServerHostConfig(createBaseUrl("/server-1"), "test-username-1", "test-password-1"),
                    new BitbucketServerHostConfig(createBaseUrl("/server-2"), "test-username-2", "test-password-2")),
                TEST_DURATION);
        underTest = new BitbucketServerClient(httpClient, objectMapper, config);

        // When
        List<Repo> returnValue = underTest.getNormalRepos();

        // Then
        assertThat(returnValue).hasSize(8);
        assertThat(returnValue).containsExactly(
                createTestRepo(1, 1, true),
                createTestRepo(1, 2, false),
                createTestRepo(1, 3, true),
                createTestRepo(1, 4, false),
                createTestRepo(2, 1, true),
                createTestRepo(2, 2, false),
                createTestRepo(2, 3, true),
                createTestRepo(2, 4, false));
    }

    @Test
    public void getNormalReposShouldThrowAnExceptionWhenBitbucketServerReturnsAnUnexpectedStatusCode() {
        // Given
        BitbucketServerConfig config = new BitbucketServerConfig(
                List.of(new BitbucketServerHostConfig(createBaseUrl("/server-does-not-exist"), "test-username-1", "test-password-1")),
                TEST_DURATION);
        underTest = new BitbucketServerClient(httpClient, objectMapper, config);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getNormalRepos());

        // Then
        assertThat(thrown).isInstanceOf(BitbucketServerClientException.class);
        BitbucketServerClientException exception = (BitbucketServerClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:" + BitbucketServerWireMockFactory.PORT + "/server-does-not-exist/rest/api/1.0/repos' failed with status 404");
        assertThat(exception.getUri()).isEqualTo("http://localhost:" + BitbucketServerWireMockFactory.PORT + "/server-does-not-exist/rest/api/1.0/repos");
        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getResponseBody()).isEqualTo("Server does not exist");
    }

    private String createBaseUrl(String path) {
        return "http://localhost:" + BitbucketServerWireMockFactory.PORT + path;
    }

    private Repo createTestRepo(int serverNumber, int repoNumber, boolean hasComponentMetadataFile) {
        return Repo.builder()
                .url("http://localhost:" + BitbucketServerWireMockFactory.PORT
                        + "/server-" + serverNumber
                        + "/scm/example-project-" + repoNumber
                        + "/example-repo-" + repoNumber + ".git")
                .hasComponentMetadataFile(hasComponentMetadataFile)
                .build();
    }
}
