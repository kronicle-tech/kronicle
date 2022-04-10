package tech.kronicle.plugins.gitlab.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.utils.HttpStatuses;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

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
        GitLabConfig config = new GitLabConfig(
                List.of(new GitLabHostConfig(baseUrl, null, null, null)),
                PAGE_SIZE,
                TIMEOUT);
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
                new Repo("https://example.com/repo-1-KRONICLE_YAML.git", true),
                new Repo("https://example.com/repo-2-KRONICLE_YAML.git", true),
                new Repo("https://example.com/repo-3-NO_DEFAULT_BRANCH.git", false),
                new Repo("https://example.com/repo-4-NONE.git", false),
                new Repo("https://example.com/repo-5-NONE.git", false),
                new Repo("https://example.com/repo-6-NONE.git", false),
                new Repo("https://example.com/repo-7-NONE.git", false),
                new Repo("https://example.com/repo-8-NONE.git", false)
        );
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitLabReturnsAnUnexpectedStatusCode() {
        // Given
        GitLabApiWireMockFactory.Scenario scenario = GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.create(scenario);
        GitLabConfig config = new GitLabConfig(null, PAGE_SIZE, TIMEOUT);
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

    private GitLabClient createUnderTest(GitLabConfig config) {
        return new GitLabClient(createHttpClient(), createJsonMapper(), config);
    }

    public static Stream<GitLabApiWireMockFactory.Scenario> provideReposResponseTypeScenarios() {
        return GitLabApiWireMockFactory.Scenario.ALL_SCENARIOS.stream()
                .filter(scenario -> !scenario.equals(GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR));
    }
}
