package tech.kronicle.plugins.gitlab.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.plugins.gitlab.GitLabPlugin;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.constants.GitLabApiHeaders;
import tech.kronicle.plugins.gitlab.constants.GitLabApiPaths;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.models.api.GitLabPipeline;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabUser;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.utils.HttpStatuses;
import tech.kronicle.utils.UriVariablesBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.common.CaseUtils.toTitleCase;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GitLabClient {

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final GitLabConfig config;
  private final Clock clock;

  public List<Repo> getRepos(String baseUrl, GitLabAccessTokenConfig accessToken) {
    return getRepos(accessToken, baseUrl, getAllProjectsUri());
  }

  public List<Repo> getRepos(String baseUrl, GitLabUserConfig user) {
    return getRepos(user.getAccessToken(), baseUrl, getUserProjectsUri(user));
  }

  public List<Repo> getRepos(String baseUrl, GitLabGroupConfig group) {
    return getRepos(group.getAccessToken(), baseUrl, getGroupProjectsUri(group));
  }

  private String getAllProjectsUri() {
    return GitLabApiPaths.PROJECTS;
  }

  private String getUserProjectsUri(GitLabUserConfig user) {
    return expandUriTemplate(GitLabApiPaths.USER_PROJECTS, Map.of("username", user.getUsername()));
  }

  private String getGroupProjectsUri(GitLabGroupConfig group) {
    return expandUriTemplate(GitLabApiPaths.GROUP_PROJECTS, Map.of("groupPath", group.getPath()));
  }

  private List<Repo> getRepos(GitLabAccessTokenConfig accessToken, String baseUrl, String uri) {
    return getAllPagedResources(accessToken, baseUrl + uri, new TypeReference<List<GitLabRepo>>() {})
            .map(mapRepo(accessToken, baseUrl))
            .collect(toUnmodifiableList());
  }

  private <T> Stream<T> getAllPagedResources(
          GitLabAccessTokenConfig accessToken,
          String uri,
          TypeReference<List<T>> bodyTypeReference
  ) {
    return Stream.iterate(
            getPagedResource(accessToken, uri, 1, bodyTypeReference),
            Objects::nonNull,
            page -> getPagedResource(accessToken, uri, page.nextPage, bodyTypeReference)
    )
            .flatMap(page -> page.items.stream());
  }

  private Function<GitLabRepo, Repo> mapRepo(
          GitLabAccessTokenConfig accessToken,
          String baseUrl
  ) {
    return repo -> {
      Repo.RepoBuilder repoBuilder = Repo.builder()
              .url(repo.getHttp_url_to_repo());
      if (nonNull(repo.getDefault_branch())) {
        repoBuilder.hasComponentMetadataFile(hasComponentMetadataFile(accessToken, baseUrl, repo))
                .state(getState(accessToken, baseUrl, repo));
      }
      return repoBuilder
              .build();
    };
  }

  private boolean hasComponentMetadataFile(
          GitLabAccessTokenConfig accessToken,
          String baseUrl,
          GitLabRepo repo
  ) {
    return KronicleMetadataFilePaths.ALL.stream().anyMatch(kronicleMetadataFilePath -> {
      Map<String, String> uriVariables = UriVariablesBuilder.builder()
              .addUriVariable("projectId", repo.getId())
              .addUriVariable("kronicleMetadataFilePath", kronicleMetadataFilePath)
              .addUriVariable("defaultBranch", repo.getDefault_branch())
              .build();
      return doesResourceExist(
              accessToken,
              expandUriTemplate(baseUrl + GitLabApiPaths.PROJECT_KRONICLE_YAML_FILE, uriVariables)
      );
    });
  }

  private ComponentState getState(
          GitLabAccessTokenConfig accessToken,
          String baseUrl,
          GitLabRepo repo
  ) {
    LocalDateTime now = LocalDateTime.now(clock);
    List<GitLabPipeline> pipelines = getProjectPipelinesForDefaultBranch(accessToken, baseUrl, repo);
    if (pipelines.isEmpty()) {
      return null;
    }
    GitLabPipeline latestPipeline = pipelines.get(0);
    List<GitLabJob> jobs = getProjectJobsForPipeline(accessToken, baseUrl, repo, latestPipeline.getId());
    if (jobs.isEmpty()) {
      return null;
    }
    return mapState(jobs, now);
  }

  private ComponentState mapState(List<GitLabJob> jobs, LocalDateTime now) {
    return ComponentState.builder()
            .environments(List.of(
                    EnvironmentState.builder()
                            .id(config.getEnvironmentId())
                            .plugins(List.of(
                                    EnvironmentPluginState.builder()
                                            .id(GitLabPlugin.ID)
                                            .checks(mapChecks(jobs, now))
                                            .build()
                            ))
                            .build()
            ))
            .build();
  }

  private List<CheckState> mapChecks(List<GitLabJob> jobs, LocalDateTime now) {
    return jobs.stream()
            .map(mapCheck(now))
            .collect(toUnmodifiableList());
  }

  private Function<GitLabJob, CheckState> mapCheck(LocalDateTime now) {
    return job -> CheckState.builder()
            .name(job.getName())
            .description("GitLab Job")
            .avatarUrl(mapAvatarUrl(job))
            .status(mapCheckStatus(job))
            .statusMessage(toTitleCase(job.getStatus()))
            .links(createWorkflowRunLinks(job))
            .updateTimestamp(now)
            .build();
  }

  private String mapAvatarUrl(GitLabJob job) {
    return Optional.of(job)
            .map(GitLabJob::getUser)
            .map(GitLabUser::getAvatar_url)
            .orElse(null);
  }

  private ComponentStateCheckStatus mapCheckStatus(GitLabJob job) {
    switch (job.getStatus()) {
      case "created":
      case "pending":
      case "running":
      case "manual":
        return ComponentStateCheckStatus.PENDING;
      case "failed":
        return ComponentStateCheckStatus.CRITICAL;
      case "success":
        return ComponentStateCheckStatus.OK;
      case "canceled":
      case "skipped":
        return ComponentStateCheckStatus.WARNING;
      default:
        log.warn("Unrecognised job status \"{}\"", job.getStatus());
        return ComponentStateCheckStatus.UNKNOWN;
    }
  }

  private List<Link> createWorkflowRunLinks(GitLabJob job) {
    if (isNull(job.getWeb_url())) {
      return List.of();
    }

    return List.of(
            Link.builder()
                    .url(job.getWeb_url())
                    .description("GitLab Job")
                    .build()
    );
  }

  private List<GitLabPipeline> getProjectPipelinesForDefaultBranch(
          GitLabAccessTokenConfig accessToken,
          String baseUrl,
          GitLabRepo repo
  ) {
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
              .addUriVariable("projectId", repo.getId())
              .addUriVariable("defaultBranch", repo.getDefault_branch())
              .build();
    PagedResource<GitLabPipeline> pagedResource = getPagedResource(
            accessToken,
            expandUriTemplate(baseUrl + GitLabApiPaths.PROJECT_PIPELINES_FOR_REF, uriVariables),
            1,
            new TypeReference<>() {}
    );
    return Optional.ofNullable(pagedResource).map(PagedResource::getItems).orElse(List.of());
  }

  private List<GitLabJob> getProjectJobsForPipeline(
          GitLabAccessTokenConfig accessToken,
          String baseUrl,
          GitLabRepo repo,
          long pipelineId
  ) {
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("projectId", repo.getId())
            .addUriVariable("pipelineId", pipelineId)
            .build();
    return getAllPagedResources(
            accessToken,
            expandUriTemplate(baseUrl + GitLabApiPaths.PROJECT_JOBS_FOR_PIPELINE, uriVariables),
            new TypeReference<List<GitLabJob>>() {}
    )
            .collect(toUnmodifiableList());
  }

  @SneakyThrows
  private <T> PagedResource<T> getPagedResource(
          GitLabAccessTokenConfig accessToken,
          String uri,
          Integer page,
          TypeReference<List<T>> bodyTypeReference
  ) {
    if (isNull(page)) {
      return null;
    }
    String separator = uri.contains("?") ? "&" : "?";
    String uriWithQueryParams = uri + separator + "page=" + page + "&per_page=" + config.getProjectPageSize();
    logWebCall(uriWithQueryParams);
    HttpRequest.Builder requestBuilder = createHttpRequestBuilder(config.getTimeout())
            .uri(URI.create(uriWithQueryParams));
    configureRequest(requestBuilder, accessToken);
    HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    checkResponseStatus(response, uriWithQueryParams);
    return new PagedResource<>(response, bodyTypeReference);
  }

  private void checkResponseStatus(HttpResponse<String> response, String uri) {
    if (response.statusCode() != HttpStatuses.OK) {
      GitLabClientException exception = new GitLabClientException(
              uri,
              response.statusCode(),
              response.body()
      );
      log.warn(exception.getMessage());
      throw exception;
    }
  }

  @SneakyThrows
  private boolean doesResourceExist(GitLabAccessTokenConfig accessToken, String uri) {
    logWebCall(uri);
    HttpRequest.Builder requestBuilder = createHttpRequestBuilder(config.getTimeout())
            .uri(URI.create(uri));
    configureRequest(requestBuilder, accessToken);
    HttpResponse<Void> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
    boolean exists = response.statusCode() == HttpStatuses.OK;
    log.info("Resource exists: {}", exists);
    return exists;
  }

  private void logWebCall(String uri) {
    log.info("Calling {}", uri);
  }

  private void configureRequest(
          HttpRequest.Builder requestBuilder,
          GitLabAccessTokenConfig accessToken
  ) {
    if (nonNull(accessToken)) {
      requestBuilder.header(GitLabApiHeaders.PRIVATE_TOKEN, accessToken.getValue());
    }
  }

  @Getter
  private class PagedResource<T> {

    private final List<T> items;
    private final Integer nextPage;

    @SneakyThrows
    public PagedResource(HttpResponse<String> response, TypeReference<List<T>> bodyTypeReference) {
      items = objectMapper.readValue(response.body(), bodyTypeReference);
      nextPage = getNextPage(response);
    }

    private Integer getNextPage(HttpResponse<String> response) {
      return getOptionalNextPage(response).map(Integer::parseInt).orElse(null);
    }

    private Optional<String> getOptionalNextPage(HttpResponse<String> response) {
      return response.headers().firstValue(GitLabApiHeaders.X_NEXT_PAGE)
              .filter(value -> !value.isEmpty());
    }
  }
}
