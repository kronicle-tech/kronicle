package tech.kronicle.plugins.github.client;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.plugins.github.config.GitHubOrganizationConfig;
import tech.kronicle.plugins.github.config.GitHubUserConfig;
import tech.kronicle.plugins.github.guice.GuiceModule;
import tech.kronicle.plugins.github.models.ApiResponseCacheEntry;
import tech.kronicle.plugins.github.models.api.GitHubContentEntry;
import tech.kronicle.plugins.github.models.api.GitHubGetWorkflowRunsResponse;
import tech.kronicle.plugins.github.models.api.GitHubRepo;
import tech.kronicle.plugins.github.models.api.GitHubRepoOwner;
import tech.kronicle.plugins.github.services.ApiResponseCache;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

@ExtendWith(MockitoExtension.class)
public class GitHubClientTest {

    private static final Clock clock = Clock.fixed(
            LocalDateTime.of(2001, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );
    private static final Duration TEST_DURATION = Duration.ofSeconds(30);

    private final GitHubApiWireMockFactory gitHubApiWireMockFactory = new GitHubApiWireMockFactory();
    private final ObjectMapper objectMapper = createJsonMapper()
            .registerModule(new JavaTimeModule());
    private GitHubClient underTest;
    @Mock
    private ApiResponseCache mockCache;
    private WireMockServer wireMockServer;
    private String baseUrl;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        baseUrl = "http://localhost:" + GitHubApiWireMockFactory.PORT;
        logCaptor = new LogCaptor(GitHubClient.class);
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
        logCaptor.close();
    }

    @ParameterizedTest
    @MethodSource("provideReposResponseTypeScenarios")
    public void getReposShouldReturnAListOfReposWithVaryingHasComponentMetadataFileValues(GitHubApiWireMockFactory.Scenario scenario) {
        // Given
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue;

        switch (scenario.getReposResourceType()) {
            case AUTHENTICATED_USER:
                returnValue = underTest.getRepos(scenario.getAccessToken());
                break;
            case USER:
                returnValue = underTest.getRepos(new GitHubUserConfig(scenario.getName(), scenario.getAccessToken()));
                break;
            case ORGANIZATION:
                returnValue = underTest.getRepos(new GitHubOrganizationConfig(scenario.getName(), scenario.getAccessToken()));
                break;
            default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.getReposResourceType());
        }

        // Then
        String reposUrl;
        switch (scenario.getReposResourceType()) {
            case AUTHENTICATED_USER:
                reposUrl = baseUrl + "/user/repos";
                break;
            case USER:
                reposUrl = baseUrl + "/users/" + scenario.getName() + "/repos";
                break;
            case ORGANIZATION:
                reposUrl = baseUrl + "/orgs/" + scenario.getName() + "/repos";
                break;
            default:
                throw new RuntimeException("Unexpected repos resource type " + scenario.getReposResourceType());
        }
        verify(mockCache).putEntry(
                scenario.getAccessToken(),
                reposUrl,
                new ApiResponseCacheEntry<>("test-modified-etag-1", List.of(
                        createGitHubRepo(scenario, 1),
                        createGitHubRepo(scenario, 2),
                        createGitHubRepo(scenario, 3),
                        createGitHubRepo(scenario, 4)
                )));
        verify(mockCache).putEntry(
                scenario.getAccessToken(),
                baseUrl + "/repos/" + scenario.getName() + "/test-repo-1/contents/",
                new ApiResponseCacheEntry<>("test-modified-etag-2", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("kronicle.yaml"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getAccessToken(),
                baseUrl + "/repos/" + scenario.getName() + "/test-repo-2/contents/",
                new ApiResponseCacheEntry<>("test-modified-etag-3", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getAccessToken(),
                baseUrl + "/repos/" + scenario.getName() + "/test-repo-3/contents/",
                new ApiResponseCacheEntry<>("test-modified-etag-4", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("kronicle.yaml"), new GitHubContentEntry("README.md"))));
        verify(mockCache).putEntry(
                scenario.getAccessToken(),
                baseUrl + "/repos/" + scenario.getName() + "/test-repo-4/contents/",
                new ApiResponseCacheEntry<>("test-modified-etag-5", List.of(new GitHubContentEntry(".gitignore"), new GitHubContentEntry("README.md"))));

        assertThat(returnValue).containsExactly(
                createRepo(scenario, 1, true),
                createRepo(scenario, 2, false),
                createRepo(scenario, 3, true),
                createRepo(scenario, 4, false)
        );
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(
                        createGetReposLogEntries(scenario, reposUrl),
                        createGetRepoDetailsLogEntries(scenario, 1),
                        createGetRepoDetailsLogEntries(scenario, 2),
                        createGetRepoDetailsLogEntries(scenario, 3),
                        createGetRepoDetailsLogEntries(scenario, 4)
                ))
        );
    }

    @Test
    public void getReposShouldLogRateLimitResponseHeadersInGitHubApiResponses() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.RATE_LIMIT_RESPONSE_HEADERS;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos(scenario.getAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                createRepo(scenario, 1, true),
                createRepo(scenario, 2, false),
                createRepo(scenario, 3, true),
                createRepo(scenario, 4, false)
        );
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactlyElementsOf(
                unmodifiableUnionOfLists(List.of(
                        createGetReposLogEntries(scenario, baseUrl + "/user/repos", 1),
                        createGetRepoDetailsLogEntries(scenario, 1, 2, 2),
                        createGetRepoDetailsLogEntries(scenario, 2, 2, 3),
                        createGetRepoDetailsLogEntries(scenario, 3, 2, 4),
                        createGetRepoDetailsLogEntries(scenario, 4, 2, 5)
                ))
        );
    }

    @Test
    public void getReposShouldReturnCachedUserReposIfETagHasNotChanged() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.ETAG_USER_REPOS_NOT_MODIFIED;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();
        ApiResponseCacheEntry<List<GitHubRepo>> userReposCacheEntry = new ApiResponseCacheEntry<>(
                "test-etag-1",
                List.of(
                        createGitHubRepo(scenario, "cached-clone-url", 1),
                        createGitHubRepo(scenario, "cached-clone-url", 2)
                )
        );
        doReturn(userReposCacheEntry).when(mockCache).getEntry(scenario.getAccessToken(), baseUrl + "/user/repos");

        // When
        List<Repo> returnValue = underTest.getRepos(scenario.getAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                createRepo("cached-clone-url", 1, true),
                createRepo("cached-clone-url", 2, false)
        );
    }

    @Test
    public void getReposShouldReturnCachedRepoRootContentsIfETagHasNotChanged() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.ETAG_REPO_2_NOT_MODIFIED;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();
        ApiResponseCacheEntry<List<GitHubContentEntry>> contentsCacheEntry = new ApiResponseCacheEntry<>("test-etag-3", List.of(new GitHubContentEntry("kronicle.yaml")));
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/user/repos")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 1 + "/contents/")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 1 + "/actions/runs?page=1")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 1 + "/actions/runs?page=2")).thenReturn(null);
        doReturn(contentsCacheEntry).when(mockCache).getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 2 + "/contents/");
        doReturn(
                new ApiResponseCacheEntry<>(
                        "test-etag-3",
                        createCachedWorkflowRuns(1)
                )
        )
                .when(mockCache).getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 2 + "/actions/runs?page=1");
        doReturn(
                new ApiResponseCacheEntry<>(
                        "test-etag-3",
                        createCachedWorkflowRuns(2)
                )
        )
                .when(mockCache).getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 2 + "/actions/runs?page=2");
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 3 + "/contents/")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 3 + "/actions/runs?page=1")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 3 + "/actions/runs?page=2")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 4 + "/contents/")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 4 + "/actions/runs?page=1")).thenReturn(null);
        when(mockCache.getEntry(scenario.getAccessToken(), baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + 4 + "/actions/runs?page=2")).thenReturn(null);

        // When
        List<Repo> returnValue = underTest.getRepos(scenario.getAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                createRepo(scenario, 1, true),
                // hasComponentMetadataFile has been changed from "false" to "true" for repo 2 by the cached response
                createRepo(scenario, 2, true, " cached"),
                createRepo(scenario, 3, true),
                createRepo(scenario, 4, false)
        );
    }

    @Test
    public void getReposShouldHandleARepoWithNoContent() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.REPO_3_NO_CONTENT;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos(scenario.getAccessToken());

        // Then
        assertThat(returnValue).containsExactly(
                createRepo(scenario, 1, true),
                createRepo(scenario, 2, false),
                // hasComponentMetadataFile has been changed from "true" to "false" for repo 3 by the 404 response
                createRepo(scenario, 3, false),
                createRepo(scenario, 4, false));
    }

    @Test
    public void getReposShouldThrowAnExceptionWhenGitHubReturnsAnUnexpectedStatusCode() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.INTERNAL_SERVER_ERROR;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRepos(scenario.getAccessToken()));

        // Then
        assertThat(thrown).isInstanceOf(GitHubClientException.class);
        GitHubClientException exception = (GitHubClientException) thrown;
        assertThat(exception).hasMessage("Call to 'http://localhost:36208/user/repos' failed with status 500");
        assertThat(exception.getUri()).isEqualTo("http://localhost:36208/user/repos");
        assertThat(exception.getStatusCode()).isEqualTo(500);
        assertThat(exception.getResponseBody()).isEqualTo("Internal Server Error");
    }

    @Test
    public void getReposShouldHandleANotFoundResponseForGetReposRequest() {
        // Given
        GitHubApiWireMockFactory.Scenario scenario = GitHubApiWireMockFactory.Scenario.REPO_LIST_NOT_FOUND;
        wireMockServer = gitHubApiWireMockFactory.create(scenario);
        underTest = createUnderTest();

        // When
        List<Repo> returnValue = underTest.getRepos(scenario.getAccessToken());

        // Then
        assertThat(returnValue).isEmpty();
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/user/repos for user " + scenario.getBasicAuthUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/user/repos for user " + scenario.getBasicAuthUsername() + ": rate limit null, remaining null, reset null, used null, resource null"),
                new SimplifiedLogEvent(Level.INFO, "Not found response for " + baseUrl + "/user/repos for user " + scenario.getBasicAuthUsername()));
    }

    public static Stream<GitHubApiWireMockFactory.Scenario> provideReposResponseTypeScenarios() {
        return Stream.of(
                GitHubApiWireMockFactory.Scenario.ACCESS_TOKEN,
                GitHubApiWireMockFactory.Scenario.USER,
                GitHubApiWireMockFactory.Scenario.USER_WITH_ACCESS_TOKEN,
                GitHubApiWireMockFactory.Scenario.ORGANIZATION,
                GitHubApiWireMockFactory.Scenario.ORGANIZATION_WITH_ACCESS_TOKEN);
    }

    private GitHubClient createUnderTest() {
        return new GitHubClient(
                createHttpClient(),
                new GuiceModule().objectMapper(),
                new GitHubConfig(
                        baseUrl,
                        null,
                        null,
                        null,
                        "test-environment-id",
                        TEST_DURATION
                ),
                mockCache,
                clock
        );
    }

    private GitHubRepo createGitHubRepo(
            GitHubApiWireMockFactory.Scenario scenario,
            int repoNumber
    ) {
        return createGitHubRepo(scenario, scenario.getName(), repoNumber);
    }

    private GitHubRepo createGitHubRepo(
            GitHubApiWireMockFactory.Scenario scenario,
            String cloneUrlScenarioName,
            int repoNumber
    ) {
        return new GitHubRepo(
                "test-repo-" + repoNumber,
                "test-repo-description-" + repoNumber,
                "https://github.com/" + cloneUrlScenarioName + "/test-repo-" + repoNumber + ".git",
                baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/contents/{+path}",
                "test-default-branch",
                new GitHubRepoOwner(
                        scenario.getName(),
                        "test-owner-avatar-url-" + repoNumber
                )
        );
    }

    @SneakyThrows
    private GitHubGetWorkflowRunsResponse createCachedWorkflowRuns(int page) {
        String json = gitHubApiWireMockFactory.readTestFile(
                "github-api-responses/repo-workflow-runs-page-" + page + ".json"
        );
        GitHubGetWorkflowRunsResponse response = objectMapper.readValue(json, GitHubGetWorkflowRunsResponse.class);
        return new GitHubGetWorkflowRunsResponse(
                response.getWorkflow_runs().stream()
                        .map(workflowRun -> workflowRun.withName(workflowRun.getName() + " cached"))
                        .collect(toUnmodifiableList())
        );
    }

    private Repo createRepo(
            GitHubApiWireMockFactory.Scenario scenario,
            int repoNumber,
            boolean hasComponentMetadataFile
    ) {
        return createRepo(scenario.getName(), repoNumber, hasComponentMetadataFile, "");
    }

    private Repo createRepo(
            GitHubApiWireMockFactory.Scenario scenario,
            int repoNumber,
            boolean hasComponentMetadataFile,
            String checkSuffix
    ) {
        return createRepo(scenario.getName(), repoNumber, hasComponentMetadataFile, checkSuffix);
    }

    private Repo createRepo(
            String scenarioName,
            int repoNumber,
            boolean hasComponentMetadataFile
    ) {
        return createRepo(scenarioName, repoNumber, hasComponentMetadataFile, "");
    }

    private Repo createRepo(
            String scenarioName,
            int repoNumber,
            boolean hasComponentMetadataFile,
            String checkSuffix
    ) {
        return Repo.builder()
                .url("https://github.com/" + scenarioName + "/test-repo-" + repoNumber + ".git")
                .description("test-repo-description-" + repoNumber)
                .defaultBranch("test-default-branch")
                .hasComponentMetadataFile(hasComponentMetadataFile)
                .state(ComponentState.builder()
                        .environments(List.of(
                                EnvironmentState.builder()
                                        .id("test-environment-id")
                                        .plugins(List.of(
                                                EnvironmentPluginState.builder()
                                                        .id("github")
                                                        .checks(List.of(
                                                                createCheckState(1, ComponentStateCheckStatus.OK, "Success", checkSuffix),
                                                                createCheckState(2, ComponentStateCheckStatus.CRITICAL, "Failure", checkSuffix),
                                                                createCheckState(4, ComponentStateCheckStatus.OK, "Success", checkSuffix),
                                                                createCheckState(5, ComponentStateCheckStatus.CRITICAL, "Failure", checkSuffix)
                                                        ))
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
                )
                .build();
    }

    private CheckState createCheckState(
            int checkStateNumber,
            ComponentStateCheckStatus status,
            String statusMessage,
            String checkSuffix
    ) {
        return CheckState.builder()
                .name("Test name " + checkStateNumber + checkSuffix)
                .description("GitHub Actions Workflow")
                .status(status)
                .statusMessage(statusMessage)
                .links(List.of(
                        Link.builder()
                                .url("https://example.com/test-html-url-" + checkStateNumber)
                                .description("GitHub Actions Workflow")
                                .build()
                ))
                .updateTimestamp(LocalDateTime.now(clock))
                .build();
    }

    private List<SimplifiedLogEvent> createGetReposLogEntries(
            GitHubApiWireMockFactory.Scenario scenario,
            String reposUrl
    ) {
        return createGetReposLogEntries(scenario, reposUrl, null);
    }

    private List<SimplifiedLogEvent> createGetReposLogEntries(
            GitHubApiWireMockFactory.Scenario scenario,
            String reposUrl,
            Integer requestNumber
    ) {
        String rateLimitMessage = createRateLimitMessage(requestNumber);
        return List.of(
                new SimplifiedLogEvent(Level.INFO, "Calling " + reposUrl + " for user " + scenario.getBasicAuthUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + reposUrl + " for user " + scenario.getBasicAuthUsername() + ": " + rateLimitMessage),
                new SimplifiedLogEvent(Level.INFO, "Response for " + reposUrl + " for user " + scenario.getBasicAuthUsername() + " was different to last call")
        );
    }

    private List<SimplifiedLogEvent> createGetRepoDetailsLogEntries(
            GitHubApiWireMockFactory.Scenario scenario,
            int repoNumber
    ) {
        return createGetRepoDetailsLogEntries(scenario, repoNumber, 2, null);
    }

    private List<SimplifiedLogEvent> createGetRepoDetailsLogEntries(
            GitHubApiWireMockFactory.Scenario scenario,
            int repoNumber,
            int pageCount,
            Integer requestNumber
    ) {
        String rateLimitMessage = createRateLimitMessage(requestNumber);
        List<SimplifiedLogEvent> logEvents = new ArrayList<>(List.of(
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/contents/ for user " + scenario.getBasicAuthUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/contents/ for user " + scenario.getBasicAuthUsername() + ": " + rateLimitMessage),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/contents/ for user " + scenario.getBasicAuthUsername() + " was different to last call"),
                new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=1 for user " + scenario.getBasicAuthUsername()),
                new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=1 for user " + scenario.getBasicAuthUsername() + ": " + rateLimitMessage),
                new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=1 for user " + scenario.getBasicAuthUsername() + " was different to last call")
        ));
        if (pageCount == 2) {
            logEvents.addAll(List.of(
                    new SimplifiedLogEvent(Level.INFO, "Calling " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=2 for user " + scenario.getBasicAuthUsername()),
                    new SimplifiedLogEvent(Level.INFO, "Request limits after call " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=2 for user " + scenario.getBasicAuthUsername() + ": " + rateLimitMessage),
                    new SimplifiedLogEvent(Level.INFO, "Response for " + baseUrl + "/repos/" + scenario.getName() + "/test-repo-" + repoNumber + "/actions/runs?page=2 for user " + scenario.getBasicAuthUsername() + " was different to last call")
            ));
        }
        return logEvents;
    }

    private String createRateLimitMessage(Integer requestNumber) {
        if (nonNull(requestNumber)) {
            return createRateLimitMessage(
                    Integer.toString(5000 + (requestNumber * 2)),
                    Integer.toString(4000 + requestNumber),
                    "2020-01-01T00:00:0" + requestNumber + "Z",
                    Integer.toString(1000 + requestNumber),
                    "test-resource-" + requestNumber
            );
        } else {
            return createRateLimitMessage(
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    private String createRateLimitMessage(
            String limit,
            String limitRemaining,
            String reset,
            String limitUsed,
            String resource) {
        return "rate limit " + limit + ", " +
                "remaining " + limitRemaining + ", " +
                "reset " + reset + ", " +
                "used " + limitUsed + ", " +
                "resource " + resource;
    }
}
