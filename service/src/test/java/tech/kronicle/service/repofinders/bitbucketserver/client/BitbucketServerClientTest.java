package tech.kronicle.service.repofinders.bitbucketserver.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.bitbucketserver.config.BitbucketServerConfig;
import tech.kronicle.service.repofinders.bitbucketserver.config.BitbucketServerHostConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = { BitbucketServerClientTestConfiguration.class})
public class BitbucketServerClientTest {

    private static final Duration TEST_DURATION = Duration.ofSeconds(30);

    private BitbucketServerClient underTest;
    @Autowired
    private WebClient webClient;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void beforeEach() {
        wireMockServer = BitbucketServerWireMockFactory.create();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getNormalReposShouldReturnAnEmptyListWhenNoReposAreFound() {
        // Given
        BitbucketServerConfig config = new BitbucketServerConfig(List.of(), TEST_DURATION);
        underTest = new BitbucketServerClient(webClient, config);

        // When
        List<ApiRepo> returnValue = underTest.getNormalRepos();

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
        underTest = new BitbucketServerClient(webClient, config);

        // When
        List<ApiRepo> returnValue = underTest.getNormalRepos();

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
        underTest = new BitbucketServerClient(webClient, config);

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

    private ApiRepo createTestRepo(int serverNumber, int repoNumber, boolean hasComponentMetadataFile) {
        return new ApiRepo(
                "http://localhost:" + BitbucketServerWireMockFactory.PORT
                        + "/server-" + serverNumber
                        + "/scm/example-project-" + repoNumber
                        + "/example-repo-" + repoNumber + ".git",
                hasComponentMetadataFile);
    }
}
