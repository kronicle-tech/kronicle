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
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.utils.HttpStatuses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
        List<Repo> returnValue;

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
                createRepo(1, "https://example.com/repo-1-KRONICLE_YAML.git", true),
                createRepo(2, "https://example.com/repo-2-KRONICLE_YAML.git", true),
                createRepo(3, "https://example.com/repo-3-NO_DEFAULT_BRANCH.git", false, false),
                createRepo(4, "https://example.com/repo-4-NONE.git", false),
                createRepo(5, "https://example.com/repo-5-NONE.git", false),
                createRepo(6, "https://example.com/repo-6-NONE.git", false),
                createRepo(7, "https://example.com/repo-7-NONE.git", false),
                createRepo(8, "https://example.com/repo-8-NONE.git", false)
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

    private Repo createRepo(int repoNumber, String url, boolean hasComponentMetadataFile) {
        return createRepo(repoNumber, url, hasComponentMetadataFile, true);
    }

    private Repo createRepo(int repoNumber, String url, boolean hasComponentMetadataFile, boolean hasDefaultBranch) {
        return Repo.builder()
                .url(url)
                .hasComponentMetadataFile(hasDefaultBranch ? hasComponentMetadataFile : null)
                .state(createRepoState(repoNumber, hasDefaultBranch))
                .build();
    }

    private ComponentState createRepoState(int repoNumber, boolean hasDefaultBranch) {
        if (!hasDefaultBranch) {
            return null;
        }
        return ComponentState.builder()
                .environments(List.of(
                        EnvironmentState.builder()
                                .id("test-environment-id")
                                .plugins(List.of(
                                        EnvironmentPluginState.builder()
                                                .id("gitlab")
                                                .checks(List.of(
                                                        createCheck(repoNumber, 1),
                                                        createCheck(repoNumber, 2),
                                                        createCheck(repoNumber, 3),
                                                        createCheck(repoNumber, 4),
                                                        createCheck(repoNumber, 5)
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private CheckState createCheck(int repoNumber, int checkNumber) {
        return CheckState.builder()
                .name("Test name " + repoNumber + " " + checkNumber)
                .description("GitLab Job")
                .avatarUrl("https://example.com/avatar-url-" + repoNumber + "-" + checkNumber)
                .status(ComponentStateCheckStatus.OK)
                .statusMessage("Success")
                .links(List.of(
                        Link.builder()
                                .url("https://example.com/web-url-" + repoNumber + "-" + checkNumber)
                                .description("GitLab Job")
                                .build()
                ))
                .updateTimestamp(LocalDateTime.of(2000 + repoNumber, checkNumber, 1, 0, 0))
                .build();
    }

    private GitLabClient createUnderTest(GitLabConfig config) {
        return new GitLabClient(createHttpClient(), new GuiceModule().objectMapper(), config);
    }

    public static Stream<GitLabApiWireMockFactory.Scenario> provideReposResponseTypeScenarios() {
        return GitLabApiWireMockFactory.Scenario.ALL_SCENARIOS.stream()
                .filter(scenario -> !scenario.equals(GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR));
    }
}
