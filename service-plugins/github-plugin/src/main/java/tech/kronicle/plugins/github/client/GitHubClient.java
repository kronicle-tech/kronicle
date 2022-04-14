package tech.kronicle.plugins.github.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.plugins.github.GitHubPlugin;
import tech.kronicle.plugins.github.models.api.GitHubGetWorkflowRunsResponse;
import tech.kronicle.plugins.github.models.api.GitHubWorkflowRun;
import tech.kronicle.plugins.github.models.api.GitHubWorkflowRunActor;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.github.config.GitHubAccessTokenConfig;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.plugins.github.config.GitHubOrganizationConfig;
import tech.kronicle.plugins.github.config.GitHubUserConfig;
import tech.kronicle.plugins.github.constants.GitHubApiHeaders;
import tech.kronicle.plugins.github.constants.GitHubApiPaths;
import tech.kronicle.plugins.github.models.ApiResponseCacheEntry;
import tech.kronicle.plugins.github.models.api.GitHubContentEntry;
import tech.kronicle.plugins.github.models.api.GitHubRepo;
import tech.kronicle.plugins.github.services.ApiResponseCache;
import tech.kronicle.utils.HttpStatuses;
import tech.kronicle.utils.UriVariablesBuilder;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.utils.BasicAuthUtils.basicAuth;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitHubClient {

  private static final List<Integer> EXPECTED_STATUS_CODES = List.of(
          HttpStatuses.OK,
          HttpStatuses.NOT_MODIFIED,
          HttpStatuses.NOT_FOUND
  );

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final GitHubConfig config;
  private final ApiResponseCache cache;
  private final Clock clock;

  public List<Repo> getRepos(GitHubAccessTokenConfig accessToken) {
    return getRepos(accessToken, getAuthenticatedUserReposUri());
  }

  public List<Repo> getRepos(GitHubUserConfig user) {
    return getRepos(user.getAccessToken(), getUserReposUri(user));
  }

  public List<Repo> getRepos(GitHubOrganizationConfig organization) {
    return getRepos(organization.getAccessToken(), getOrganizationReposUri(organization));
  }

  private String getAuthenticatedUserReposUri() {
    return config.getApiBaseUrl() + GitHubApiPaths.AUTHENTICATED_USER_REPOS;
  }

  private String getUserReposUri(GitHubUserConfig user) {
    return expandUriTemplate(config.getApiBaseUrl() + GitHubApiPaths.USER_REPOS, Map.of("username", user.getAccountName()));
  }

  private String getOrganizationReposUri(GitHubOrganizationConfig organization) {
    return expandUriTemplate(config.getApiBaseUrl() + GitHubApiPaths.ORGANIZATION_REPOS, Map.of("org", organization.getAccountName()));
  }

  private List<Repo> getRepos(GitHubAccessTokenConfig accessToken, String uri) {
    List<GitHubRepo> repos = getGitHubRepos(accessToken, uri);
    if (isNull(repos)) {
      return List.of();
    }
    return repos.stream()
            .map(addDataToRepo(accessToken))
            .collect(Collectors.toList());
  }

  private List<GitHubRepo> getGitHubRepos(GitHubAccessTokenConfig accessToken, String uri) {
    return getResource(accessToken, uri, new TypeReference<>() {});
  }

  private Function<GitHubRepo, Repo> addDataToRepo(GitHubAccessTokenConfig accessToken) {
    return gitHubRepo -> Repo.builder()
            .url(gitHubRepo.getClone_url())
            .description(gitHubRepo.getDescription())
            .hasComponentMetadataFile(hasComponentMetadataFile(accessToken, gitHubRepo))
            .state(getState(accessToken, gitHubRepo))
            .build();
  }

  private boolean hasComponentMetadataFile(GitHubAccessTokenConfig accessToken, GitHubRepo repo) {
    String uriTemplate = repo.getContents_url();
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("+path", "")
            .build();
    List<GitHubContentEntry> contentEntries = getResource(accessToken, expandUriTemplate(uriTemplate, uriVariables),
            new TypeReference<>() {});
    if (isNull(contentEntries)) {
      return false;
    }
    return contentEntries.stream()
            .anyMatch(contentEntry -> KronicleMetadataFilePaths.ALL.contains(contentEntry.getName()));
  }

  private ComponentState getState(GitHubAccessTokenConfig accessToken, GitHubRepo repo) {
    LocalDateTime now = LocalDateTime.now(clock);
    String uriTemplate = config.getApiBaseUrl() + GitHubApiPaths.REPO_ACTIONS_RUNS;
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("owner", repo.getOwner().getLogin())
            .addUriVariable("repo", repo.getName())
            .addUriVariable("branch", repo.getDefault_branch())
            .build();
    GitHubGetWorkflowRunsResponse response = getResource(
            accessToken,
            expandUriTemplate(uriTemplate, uriVariables),
            new TypeReference<>() {}
    );
    if (response.getWorkflow_runs().isEmpty()) {
      return null;
    }
    return ComponentState.EMPTY
            .withUpdatedEnvironment(
                    config.getEnvironmentId(),
                    environment -> environment.withUpdatedPlugin(
                            GitHubPlugin.ID,
                            plugin -> plugin.withChecks(mapWorkflowRuns(response.getWorkflow_runs(), now))
                    )
            );
  }

  private Stream<GitHubWorkflowRun> getWorkflowRunsForMostRecentCommit(List<GitHubWorkflowRun> workflowRuns) {
    GitHubWorkflowRun latestWorkflowRun = workflowRuns.get(0);
    String mostRecentSha = latestWorkflowRun.getHead_sha();
    return workflowRuns.stream()
            .filter(it -> Objects.equals(it.getHead_sha(), mostRecentSha));
  }

  private List<CheckState> mapWorkflowRuns(List<GitHubWorkflowRun> workflowRuns, LocalDateTime now) {
    return getWorkflowRunsForMostRecentCommit(workflowRuns)
            .map(mapWorkflowRun(now))
            .collect(toUnmodifiableList());
  }

  private Function<GitHubWorkflowRun, CheckState> mapWorkflowRun(LocalDateTime now) {
    return workflowRun -> {
      WorkflowRunStatus status = mapWorkflowRunStatus(workflowRun);
      return CheckState.builder()
              .status(status.status)
              .name(workflowRun.getName())
              .description("GitHub Actions Workflow")
              .avatarUrl(mapAvatarUrl(workflowRun))
              .statusMessage(status.statusMessage)
              .links(createWorkflowRunLinks(workflowRun))
              .updateTimestamp(now)
              .build();
    };
  }

  private WorkflowRunStatus mapWorkflowRunStatus(GitHubWorkflowRun workflowRun) {
    switch (workflowRun.getStatus()) {
      case "queued":
        return new WorkflowRunStatus(
                ComponentStateCheckStatus.PENDING,
                "Queued"
        );
      case "in_progress":
        return new WorkflowRunStatus(
                ComponentStateCheckStatus.PENDING,
                "In progress"
        );
      case "completed":
        switch (workflowRun.getConclusion()) {
          case "success":
            return new WorkflowRunStatus(
                    ComponentStateCheckStatus.OK,
                    "Success"
            );
          case "failure":
            return new WorkflowRunStatus(
                    ComponentStateCheckStatus.CRITICAL,
                    "Failure"
            );
          default:
            log.warn("Unrecognised workflow run conclusion \"{}\"", workflowRun.getConclusion());
            return new WorkflowRunStatus(
                    ComponentStateCheckStatus.UNKNOWN,
                    workflowRun.getConclusion()
            );
        }
      default:
        log.warn("Unrecognised workflow run status \"{}\"", workflowRun.getStatus());
        return new WorkflowRunStatus(
                ComponentStateCheckStatus.UNKNOWN,
                workflowRun.getStatus()
        );
    }
  }

  private String mapAvatarUrl(GitHubWorkflowRun workflowRun) {
    return Optional.of(workflowRun)
            .map(GitHubWorkflowRun::getActor)
            .map(GitHubWorkflowRunActor::getAvatar_url)
            .orElse(null);
  }

  private List<Link> createWorkflowRunLinks(GitHubWorkflowRun gitHubWorkflowRun) {
    if (isNull(gitHubWorkflowRun.getHtml_url())) {
      return List.of();
    }

    return List.of(
            Link.builder()
                    .url(gitHubWorkflowRun.getHtml_url())
                    .description("GitHub Actions Workflow")
                    .build()
    );
  }

  @SneakyThrows
  private <T> T getResource(GitHubAccessTokenConfig accessToken, String uri, TypeReference<T> bodyTypeReference) {
    logWebCall(accessToken, uri);
    ApiResponseCacheEntry<T> cacheEntry = cache.getEntry(accessToken, uri);
    HttpRequest.Builder requestBuilder = createHttpRequestBuilder(config.getTimeout())
            .uri(URI.create(uri));
    configureRequest(requestBuilder, accessToken, cacheEntry);
    HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    checkResponseStatus(response, uri);
    logRateLimitDetails(accessToken, uri, response);
    if (Objects.equals(response.statusCode(), HttpStatuses.NOT_MODIFIED)) {
      logNotModifiedResponse(accessToken, uri);
      return cacheEntry.getResponseBody();
    } else if (Objects.equals(response.statusCode(), HttpStatuses.NOT_FOUND)) {
      logNotFoundResponse(accessToken, uri);
      return null;
    } else {
      logWasModifiedResponse(accessToken, uri);
      T responseBody = objectMapper.readValue(response.body(), bodyTypeReference);
      cache.putEntry(accessToken, uri, createCacheEntry(response, responseBody));
      return responseBody;
    }
  }

  private void logRateLimitDetails(GitHubAccessTokenConfig accessToken, String uri, HttpResponse<String> response) {
    if (log.isInfoEnabled()) {
      log.info("Request limits after call {} for user {}: rate limit {}, remaining {}, reset {}, used {}, resource {}",
              uri,
              getUsername(accessToken),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_LIMIT),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_REMAINING),
              formatRateLimitResetTimestamp(getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESET)),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_USED),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESOURCE));
    }
  }

  private void logNotModifiedResponse(GitHubAccessTokenConfig accessToken, String uri) {
    log.info("Response for {} for user {} was same as last call", uri, getUsername(accessToken));
  }

  private void logNotFoundResponse(GitHubAccessTokenConfig accessToken, String uri) {
    log.info("Not found response for {} for user {}", uri, getUsername(accessToken));
  }

  private void logWasModifiedResponse(GitHubAccessTokenConfig accessToken, String uri) {
    log.info("Response for {} for user {} was different to last call", uri, getUsername(accessToken));
  }

  @NotEmpty
  private String getUsername(GitHubAccessTokenConfig accessToken) {
    return nonNull(accessToken) ? accessToken.getUsername() : "anonymous";
  }

  private String formatRateLimitResetTimestamp(String value) {
    if (isNull(value)) {
      return null;
    }
    return DateTimeFormatter.ISO_INSTANT.format(epochSecondsToZonedDateTime(Long.parseLong(value)));
  }

  private ZonedDateTime epochSecondsToZonedDateTime(long epochSeconds) {
    return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
  }

  private <T> ApiResponseCacheEntry<T> createCacheEntry(HttpResponse<String> response, T responseBody) {
    return new ApiResponseCacheEntry<>(getETagFromResponse(response), responseBody);
  }

  private String getETagFromResponse(HttpResponse<String> response) {
    return getResponseHeader(response, HttpHeaders.ETAG);
  }

  private String getResponseHeader(HttpResponse<String> response, String headerName) {
    return response.headers().firstValue(headerName).orElse(null);
  }

  private void logWebCall(GitHubAccessTokenConfig accessToken, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {} for user {}", uri, getUsername(accessToken));
    }
  }

  private void configureRequest(
          HttpRequest.Builder requestBuilder,
          GitHubAccessTokenConfig accessToken,
          ApiResponseCacheEntry<?> cacheEntry
  ) {
    if (nonNull(accessToken)) {
      requestBuilder.header("Authorization", basicAuth(accessToken.getUsername(), accessToken.getValue()));
    }
    if (nonNull(cacheEntry)) {
      requestBuilder.header(HttpHeaders.IF_NONE_MATCH, cacheEntry.getETag());
    }
  }

  private void checkResponseStatus(HttpResponse<String> response, String uri) {
    if (!EXPECTED_STATUS_CODES.contains(response.statusCode())) {
      GitHubClientException exception = new GitHubClientException(
              uri,
              response.statusCode(),
              response.body()
      );
      log.warn(exception.getMessage());
      throw exception;
    }
  }

  @RequiredArgsConstructor
  private static class WorkflowRunStatus {

    private final ComponentStateCheckStatus status;
    private final String statusMessage;
  }
}
