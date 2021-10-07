package tech.kronicle.service.repofinders.github.client;

import ch.qos.logback.classic.Level;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubContentEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubRepo;
import tech.kronicle.service.repofinders.github.services.ApiResponseCache;
import tech.kronicle.service.testutils.LogCaptor;
import tech.kronicle.service.testutils.SimplifiedLogEvent;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {GitHubClientTestConfiguration.class})
public class GitHubClientTest {

    private static final String TEST_PERSONAL_ACCESS_TOKEN = "test-personal-access-token";
    private static final Duration TEST_DURATION = Duration.ofSeconds(30);

    private GitHubClient underTest;
    @Autowired
    private WebClient webClient;
    @Mock
    private ApiResponseCache mockCache;
    private WireMockServer wireMockServer;
    private String baseUrl;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        wireMockServer = GitHubApiWireMockFactory.create();
        baseUrl = "http://localhost:" + GitHubApiWireMockFactory.PORT;
        logCaptor = new LogCaptor(GitHubClient.class);
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getReposShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.SIMPLE;
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, null, null, TEST_DURATION);
        underTest = new GitHubClient(webClient, config, mockCache, baseUrl);

        // When
        List<ApiRepo> returnValue = underTest.getRepos(scenario.getPersonalAccessToken());

        // Then
        verify(mockCache).putEntry(
                scenario.getPersonalAccessToken(),
                baseUrl + "/user/repos",
                // Note: The instance of Jetty that is bundled with WireMock adds "--gzip" to the end of the ETag HTTP response
                // header.  See http://wiremock.org/docs/extending-wiremock/ for confirmation
                new ApiResponseCacheEntry<>("test-modified-etag-1--gzip", List.of(
                        new GitHubRepo("https://github.com/" + scenario.getUsername() + "/test-repo-1.git", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/{+path}"),
                        new GitHubRepo("https://github.com/" + scenario.getUsername() + "/test-repo-2.git", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/{+path}"),
                        new GitHubRepo("https://github.com/" + scenario.getUsername() + "/test-repo-3.git", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/{+path}"),
                        new GitHubRepo("https://github.com/" + scenario.getUsername() + "/test-repo-4.git", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/{+path}"))));
        verify(mockCache).putEntry(
                scenario.getPersonalAccessToken(),
                baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/",
                // Note: The instance of Jetty that is bundled with WireMock adds "--gzip" to the end of the ETag HTTP response
                // header.  See http://wiremock.org/docs/extending-wiremock/ for confirmation
                new ApiResponseCacheEntry<>("test-modified-etag-2--gzip", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("kronicle.yaml"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getPersonalAccessToken(),
                baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/",
                // Note: The instance of Jetty that is bundled with WireMock adds "--gzip" to the end of the ETag HTTP response
                // header.  See http://wiremock.org/docs/extending-wiremock/ for confirmation
                new ApiResponseCacheEntry<>("test-modified-etag-3--gzip", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getPersonalAccessToken(),
                baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/",
                // Note: The instance of Jetty that is bundled with WireMock adds "--gzip" to the end of the ETag HTTP response
                // header.  See http://wiremock.org/docs/extending-wiremock/ for confirmation
                new ApiResponseCacheEntry<>("test-modified-etag-4--gzip", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("component-metadata.yaml"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getPersonalAccessToken(),
                baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/",
                // Note: The instance of Jetty that is bundled with WireMock adds "--gzip" to the end of the ETag HTTP response
                // header.  See http://wiremock.org/docs/extending-wiremock/ for confirmation
                new ApiResponseCacheEntry<>("test-modified-etag-5--gzip", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("README.md"))));

        assertThat(returnValue).containsExactly(
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 1 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 2 + ".git", false),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 3 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 4 + ".git", false));
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/user/repos for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/user/repos for user " + scenario.getUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/user/repos for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername() + " was different to last call"));
    }

    @Test
    public void getReposShouldLogRateLimitResponseHeadersInGitHubApiResponses() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.RATE_LIMIT_RESPONSE_HEADERS;
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, null, null, TEST_DURATION);
        underTest = new GitHubClient(webClient, config, mockCache, baseUrl);

        // When
        List<ApiRepo> returnValue = underTest.getRepos(scenario.getPersonalAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 1 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 2 + ".git", false),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 3 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 4 + ".git", false));
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/user/repos for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/user/repos for user " + scenario.getUsername() + ": rate limit 5002, remaining 4001, reset 2020-01-01T00:00:01Z, used 1001, resource test-resource-1"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/user/repos for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername() + ": rate limit 5004, remaining 4002, reset 2020-01-01T00:00:02Z, used 1002, resource test-resource-2"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-1/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername() + ": rate limit 5006, remaining 4003, reset 2020-01-01T00:00:03Z, used 1003, resource test-resource-3"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-2/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername() + ": rate limit 5008, remaining 4004, reset 2020-01-01T00:00:04Z, used 1004, resource test-resource-4"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-3/contents/ for user " + scenario.getUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername() + ": rate limit 5010, remaining 4005, reset 2020-01-01T00:00:05Z, used 1005, resource test-resource-5"),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-4/contents/ for user " + scenario.getUsername() + " was different to last call"));
    }

    @Test
    public void getReposShouldReturnCachedUserReposIfETagHasNotChanged() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.ETAG_USER_REPOS_NOT_MODIFIED;
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, null, null, TEST_DURATION);
        underTest = new GitHubClient(webClient, config, mockCache, baseUrl);
        ApiResponseCacheEntry<List<GitHubRepo>> userReposCacheEntry = new ApiResponseCacheEntry<>(
                "test-etag-1",
                List.of(
                        new GitHubRepo("https://example.com/cached-clone-url-1", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 1 + "/contents/{+path}"),
                        new GitHubRepo("https://example.com/cached-clone-url-2", baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 2 + "/contents/{+path}")));
        doReturn(userReposCacheEntry).when(mockCache).getEntry(scenario.getPersonalAccessToken(), baseUrl + "/user/repos");

        // When
        List<ApiRepo> returnValue = underTest.getRepos(scenario.getPersonalAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                new ApiRepo("https://example.com/cached-clone-url-1", true),
                new ApiRepo("https://example.com/cached-clone-url-2", false));
    }

    @Test
    public void getReposShouldReturnCachedRepoRootContentsIfETagHasNotChanged() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.ETAG_REPO_2_NOT_MODIFIED;
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, null, null, TEST_DURATION);
        underTest = new GitHubClient(webClient, config, mockCache, baseUrl);
        ApiResponseCacheEntry<List<GitHubContentEntry>> userReposCacheEntry = new ApiResponseCacheEntry<>("test-etag-3", List.of(new GitHubContentEntry("kronicle.yaml")));
        when(mockCache.getEntry(scenario.getPersonalAccessToken(), baseUrl + "/user/repos")).thenReturn(null);
        when(mockCache.getEntry(scenario.getPersonalAccessToken(), baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 1 + "/contents/")).thenReturn(null);
        doReturn(userReposCacheEntry).when(mockCache).getEntry(scenario.getPersonalAccessToken(), baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 2 + "/contents/");
        when(mockCache.getEntry(scenario.getPersonalAccessToken(), baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 3 + "/contents/")).thenReturn(null);
        when(mockCache.getEntry(scenario.getPersonalAccessToken(), baseUrl + "/repos/" + scenario.getUsername() + "/test-repo-" + 4 + "/contents/")).thenReturn(null);

        // When
        List<ApiRepo> returnValue = underTest.getRepos(scenario.getPersonalAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 1 + ".git", true),
                // hasComponentMetadataFile has been changed from "false" to "true" for repo 2 by the cached response
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 2 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 3 + ".git", true),
                new ApiRepo("https://github.com/" + scenario.getUsername() + "/test-repo-" + 4 + ".git", false));
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitHubReturnsAnUnexpectedStatusCode() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        GitHubRepoFinderConfig config = new GitHubRepoFinderConfig(null, null, null, TEST_DURATION);
        underTest = new GitHubClient(webClient, config, mockCache, baseUrl);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos(scenario.getPersonalAccessToken()));

        // Then
        assertThat(thrown).isInstanceOf(GitHubClientException.class);
        GitHubClientException exception = (GitHubClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:36208/user/repos' failed with status 500");
        assertThat(exception.getUri()).isEqualTo("http://localhost:36208/user/repos");
        assertThat(exception.getStatusCode()).isEqualTo(500);
        assertThat(exception.getResponseBody()).isEqualTo("Internal Server Error");
    }
}
