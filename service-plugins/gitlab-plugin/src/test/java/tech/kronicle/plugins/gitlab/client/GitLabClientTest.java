package tech.kronicle.plugins.gitlab.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.guice.GuiceModule;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.testutils.RepoScenario;
import tech.kronicle.utils.HttpStatuses;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.plugins.gitlab.testutils.EnrichedGitLabRepoUtils.createEnrichedGitLabRepo;
import static tech.kronicle.plugins.gitlab.testutils.GitLabJobUtils.createGitLabJob;
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
    @MethodSource("provideRepoScenarios")
    public void getReposShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues(
            GitLabApiWireMockFactory.ReposScenario scenario
    ) {
        // Given
        wireMockServer = gitLabApiWireMockFactory.createRepoRequests(scenario);
        underTest = createUnderTest();

        // When
        List<EnrichedGitLabRepo> returnValue;

        switch (scenario.getType()) {
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
                throw new RuntimeException("Unexpected repos resource type " + scenario.getType());
        }

        // Then
        assertThat(returnValue).containsExactly(
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 1, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 2, RepoScenario.NORMAL),
                createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 3, RepoScenario.NO_DEFAULT_BRANCH),
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
        GitLabApiWireMockFactory.ReposScenario scenario = GitLabApiWireMockFactory.ReposScenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.createRepoRequests(scenario);
        underTest = createUnderTest();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos(baseUrl, scenario.getAccessToken()));

        // Then
        assertThat(thrown).isInstanceOf(GitLabClientException.class);
        GitLabClientException exception = (GitLabClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:36209/api/v4/projects?page=1&per_page=5' failed with status 500");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatuses.INTERNAL_SERVER_ERROR);
        assertThat(exception.getUri()).isEqualTo("http://localhost:36209/api/v4/projects?page=1&per_page=5");
    }

    @ParameterizedTest
    @MethodSource("provideJobScenarios")
    public void getJobsShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues(
            GitLabApiWireMockFactory.JobsScenario scenario
    ) {
        // Given
        wireMockServer = gitLabApiWireMockFactory.createJobRequests(scenario);
        EnrichedGitLabRepo repo = createEnrichedGitLabRepo(baseUrl, scenario.getAccessToken(), 1, RepoScenario.NORMAL);
        underTest = createUnderTest();

        // When
        List<GitLabJob> returnValue = underTest.getJobs(repo);

        // Then
        if (scenario.type == GitLabApiWireMockFactory.JobsScenarioType.PIPELINES_FORBIDDEN) {
            assertThat(returnValue).isEmpty();
        } else {
            assertThat(returnValue).containsExactly(
                    createGitLabJob(1),
                    createGitLabJob(2),
                    createGitLabJob(3),
                    createGitLabJob(4),
                    createGitLabJob(5)
            );
        }
    }

    @Test
    public void getJobsShouldThrowAnExceptionWhenGitLabReturnsAnUnexpectedStatusCode() {
        // Given
        GitLabApiWireMockFactory.JobsScenario scenario = GitLabApiWireMockFactory.JobsScenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.createJobRequests(scenario);
        EnrichedGitLabRepo repo = createEnrichedGitLabRepo(
                baseUrl,
                scenario.accessToken,
                1,
                RepoScenario.NORMAL
        );
        underTest = createUnderTest();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getJobs(repo));

        // Then
        assertThat(thrown).isInstanceOf(GitLabClientException.class);
        GitLabClientException exception = (GitLabClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:36209/api/v4/projects/1/pipelines?ref=branch-1&page=1&per_page=5' failed with status 500");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatuses.INTERNAL_SERVER_ERROR);
        assertThat(exception.getUri()).isEqualTo("http://localhost:36209/api/v4/projects/1/pipelines?ref=branch-1&page=1&per_page=5");
    }

    private GitLabConfig createConfig() {
        return new GitLabConfig(
                null,
                PAGE_SIZE,
                "test-environment-id",
                TIMEOUT,
                null
        );
    }

    private GitLabClient createUnderTest() {
        return new GitLabClient(
                createHttpClient(),
                new GuiceModule().objectMapper(),
                createConfig()
        );
    }

    public static Stream<GitLabApiWireMockFactory.ReposScenario> provideRepoScenarios() {
        return GitLabApiWireMockFactory.ReposScenario.NORMAL_SCENARIOS.stream();
    }

    public static Stream<GitLabApiWireMockFactory.JobsScenario> provideJobScenarios() {
        return GitLabApiWireMockFactory.JobsScenario.NORMAL_SCENARIOS.stream();
    }
}
