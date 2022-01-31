package tech.kronicle.service.repofinders.gitlab.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderGroupConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderHostConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderUserConfig;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
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
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(
                List.of(new GitLabRepoFinderHostConfig(baseUrl, null, null, null)),
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
                returnValue = underTest.getRepos(baseUrl, new GitLabRepoFinderUserConfig("example-username", scenario.getAccessToken()));
                break;
            case GROUP:
                returnValue = underTest.getRepos(baseUrl, new GitLabRepoFinderGroupConfig("example-group-path", scenario.getAccessToken()));
                break;
            default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.getReposResourceType());
        }

        // Then
        assertThat(returnValue).hasSize(8);
        assertThat(returnValue.get(0)).isEqualTo(new ApiRepo("https://example.com/repo-1.git", true));
        assertThat(returnValue.get(1)).isEqualTo(new ApiRepo("https://example.com/repo-2.git", true));
        IntStream.range(2, returnValue.size()).forEach(index ->
                assertThat(returnValue.get(index)).isEqualTo(
                        new ApiRepo("https://example.com/repo-" + (index + 1) + ".git", false)));
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitLabReturnsAnUnexpectedStatusCode() {
        // Given
        GitLabApiWireMockFactory.Scenario scenario = GitLabApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitLabApiWireMockFactory.create(scenario);
        GitLabRepoFinderConfig config = new GitLabRepoFinderConfig(null, PAGE_SIZE, TIMEOUT);
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
