package tech.kronicle.plugins.gitlab.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.guice.GuiceModule;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.testutils.RepoScenario;
import tech.kronicle.utils.HttpStatuses;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepo;
import static tech.kronicle.plugins.gitlab.testutils.RepoScenario.NO_DEFAULT_BRANCH;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;

public class GitLabClientTest {

    private static final String baseUrl = "http://localhost:" + GitLabApiWireMockFactory.PORT;
    private static final int PAGE_SIZE = 5;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final GitLabApiWireMockFactory gitLabApiWireMockFactory = new GitLabApiWireMockFactory();
    private GitLabClient underTest;
    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @ParameterizedTest
    @MethodSource("provideReposResponseTypeScenarios")
    public void getReposShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues(GitLabApiWireMockFactory.Scenario scenario) {
        // Given
        wireMockServer = gitLabApiWireMockFactory.create(scenario);
        GitLabConfig config = createConfig(List.of(
                new GitLabHostConfig(baseUrl, null, null, null)
        ));
        underTest = createUnderTest(config);

        // When
        List<EnrichedGitLabRepo> returnValue;

        switch (scenario.getReposResourceType()) {
            case ALL:
                returnValue = underTest.getRepos(baseUrl, scenario.getAccessToken());
                break;
            case USER:
                returnValue = underTest.getRepos(baseUrl, new GitLabUserConfig("example-username", scenario.getAccessToken()));
                break;
            case GROUP:
                returnValue = underTest.getRepos(baseUrl, new GitLabGroupConfig("example-group-path", scenario.getAccessToken()));
                break;
            default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.getReposResourceType());
        }

        // Then
        assertThat(returnValue).containsExactly(
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 1, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 2, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 3, NO_DEFAULT_BRANCH),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 4, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 5, RepoScenario.NO_KRONICLE_METADATA_FILE),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 6, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 7, RepoScenario.PIPELINES_FORBIDDEN),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 8, RepoScenario.NORMAL)
        );
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitLabReturnsAnUnexpectedStatusCode() {
        // Given
        GitLabApiWireMockFactory.Scenario scenario = GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.create(scenario);
        GitLabConfig config = createConfig(null);
        underTest = createUnderTest(config);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos(baseUrl, scenario.getAccessToken()));

        // Then
        assertThat(thrown).isInstanceOf(GitLabClientException.class);
        GitLabClientException exception = (GitLabClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:36209/api/v4/projects?page=1&per_page=5' failed with status 500");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatuses.INTERNAL_SERVER_ERROR);
        assertThat(exception.getUri()).isEqualTo("http://localhost:36209/api/v4/projects?page=1&per_page=5");
    }

    private GitLabConfig createConfig(List<GitLabHostConfig> hosts) {
        return new GitLabConfig(
                hosts,
                PAGE_SIZE,
                "test-environment-id",
                TIMEOUT
        );
    }

    private GitLabClient createUnderTest(GitLabConfig config) {
        return new GitLabClient(
                createHttpClient(),
                new GuiceModule().objectMapper(),
                config
        );
    }

    public static Stream<GitLabApiWireMockFactory.Scenario> provideReposResponseTypeScenarios() {
        return GitLabApiWireMockFactory.Scenario.ALL_SCENARIOS.stream()
                .filter(scenario -> !scenario.equals(GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR));
    }
}
