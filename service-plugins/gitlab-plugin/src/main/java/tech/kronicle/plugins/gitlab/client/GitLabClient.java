package tech.kronicle.plugins.gitlab.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.constants.GitLabApiHeaders;
import tech.kronicle.plugins.gitlab.constants.GitLabApiPaths;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.models.api.GitLabPipeline;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.PagedResource;
import tech.kronicle.utils.HttpStatuses;
import tech.kronicle.utils.UriVariablesBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GitLabClient {

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final GitLabConfig config;

  public List<EnrichedGitLabRepo> getRepos(String baseUrl, GitLabAccessTokenConfig accessToken) {
    return getRepos(accessToken, baseUrl, getAllProjectsUri());
  }

  public List<EnrichedGitLabRepo> getRepos(String baseUrl, GitLabUserConfig user) {
    return getRepos(user.getAccessToken(), baseUrl, getUserProjectsUri(user));
  }

  public List<EnrichedGitLabRepo> getRepos(String baseUrl, GitLabGroupConfig group) {
    return getRepos(group.getAccessToken(), baseUrl, getGroupProjectsUri(group));
  }

  public List<GitLabJob> getJobs(
          EnrichedGitLabRepo repo
  ) {
    List<GitLabPipeline> pipelines = getProjectPipelinesForDefaultBranch(repo);
    if (pipelines.isEmpty()) {
      return List.of();
    }
    GitLabPipeline latestPipeline = pipelines.get(0);
    return getProjectJobsForPipeline(repo, latestPipeline.getId());
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

  private List<EnrichedGitLabRepo> getRepos(GitLabAccessTokenConfig accessToken, String baseUrl, String uri) {
    return getAllPagedResources(accessToken, baseUrl + uri, new TypeReference<List<GitLabRepo>>() {})
            .map(enrichRepo(baseUrl, accessToken))
            .collect(toUnmodifiableList());
  }

  private Function<GitLabRepo, EnrichedGitLabRepo> enrichRepo(String baseUrl, GitLabAccessTokenConfig accessToken) {
    return repo -> EnrichedGitLabRepo.builder()
            .repo(repo)
            .baseUrl(baseUrl)
            .accessToken(accessToken)
            .hasComponentMetadataFile(hasComponentMetadataFile(accessToken, baseUrl, repo))
            .build();
  }

  private <T> Stream<T> getAllPagedResources(
          GitLabAccessTokenConfig accessToken,
          String uri,
          TypeReference<List<T>> bodyTypeReference
  ) {
    return Stream.iterate(
            getPagedResource(accessToken, uri, 1, bodyTypeReference),
            Objects::nonNull,
            page -> getPagedResource(accessToken, uri, page.getNextPage(), bodyTypeReference)
    )
            .flatMap(page -> page.getItems().stream());
  }

  private boolean hasComponentMetadataFile(
          GitLabAccessTokenConfig accessToken,
          String baseUrl,
          GitLabRepo repo
  ) {
    if (isNull(repo.getDefault_branch())) {
      return false;
    }
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

  private List<GitLabPipeline> getProjectPipelinesForDefaultBranch(
          EnrichedGitLabRepo repo
  ) {
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
              .addUriVariable("projectId", repo.getRepo().getId())
              .addUriVariable("defaultBranch", repo.getRepo().getDefault_branch())
              .build();
    PagedResource<GitLabPipeline> pagedResource = getPagedResource(
            repo.getAccessToken(),
            expandUriTemplate(repo.getBaseUrl() + GitLabApiPaths.PROJECT_PIPELINES_FOR_REF, uriVariables),
            1,
            new TypeReference<>() {}
    );
    return Optional.ofNullable(pagedResource).map(PagedResource::getItems).orElse(List.of());
  }

  private List<GitLabJob> getProjectJobsForPipeline(
          EnrichedGitLabRepo repo,
          long pipelineId
  ) {
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("projectId", repo.getRepo().getId())
            .addUriVariable("pipelineId", pipelineId)
            .build();
    return getAllPagedResources(
            repo.getAccessToken(),
            expandUriTemplate(repo.getBaseUrl() + GitLabApiPaths.PROJECT_JOBS_FOR_PIPELINE, uriVariables),
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
    if (response.statusCode() == HttpStatuses.FORBIDDEN) {
      return PagedResource.empty();
    }
    checkResponseStatus(response, uriWithQueryParams);
    return new PagedResource<>(response, bodyTypeReference, objectMapper);
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

}
