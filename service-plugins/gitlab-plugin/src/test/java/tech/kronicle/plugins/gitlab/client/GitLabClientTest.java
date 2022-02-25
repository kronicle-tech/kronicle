package tech.kronicle.plugins.gitlab.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {GitLabClientTestConfiguration.class})
public class GitLabClientTest {

    private static final String baseUrl = "http://localhost:" + GitLabApiWireMockFactory.PORT;
    private static final int PAGE_SIZE = 5;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final GitLabApiWireMockFactory gitLabApiWireMockFactory = new GitLabApiWireMockFactory();
    private GitLabClient underTest;
    @Autowired
    private WebClient webClient;
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
        underTest = new GitLabClient(webClient, config);

        // When
        List<ApiRepo> returnValue;

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
                new ApiRepo("https://example.com/repo-1-KRONICLE_YAML.git", true),
                new ApiRepo("https://example.com/repo-2-KRONICLE_YAML.git", true),
                new ApiRepo("https://example.com/repo-3-NO_DEFAULT_BRANCH.git", false),
                new ApiRepo("https://example.com/repo-4-NONE.git", false),
                new ApiRepo("https://example.com/repo-5-NONE.git", false),
                new ApiRepo("https://example.com/repo-6-NONE.git", false),
                new ApiRepo("https://example.com/repo-7-NONE.git", false),
                new ApiRepo("https://example.com/repo-8-NONE.git", false)
        );
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitLabReturnsAnUnexpectedStatusCode() {
        // Given
        GitLabApiWireMockFactory.Scenario scenario = GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.create(scenario);
        GitLabConfig config = new GitLabConfig(null, PAGE_SIZE, TIMEOUT);
        underTest = new GitLabClient(webClient, config);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos(baseUrl, scenario.getAccessToken()));

        // Then
        assertThat(thrown).isInstanceOf(WebClientResponseException.class);
        WebClientResponseException exception = (WebClientResponseException) thrown;
        assertThat(exception).hasMessage("500 Internal Server Error from GET http://localhost:36209/api/v4/projects?page=1&per_page=5");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static Stream<GitLabApiWireMockFactory.Scenario> provideReposResponseTypeScenarios() {
        return GitLabApiWireMockFactory.Scenario.ALL_SCENARIOS.stream()
                .filter(scenario -> !scenario.equals(GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR));
    }
}
