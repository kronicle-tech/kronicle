package tech.kronicle.plugins.gitlab.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.constants.GitLabApiHeaders;
import tech.kronicle.plugins.gitlab.constants.GitLabApiPaths;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GitLabClient {

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final GitLabConfig config;

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
            .map(addHasComponentMetadataFile(accessToken, baseUrl))
            .collect(Collectors.toList());
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

  private Function<GitLabRepo, Repo> addHasComponentMetadataFile(
          GitLabAccessTokenConfig accessToken,
          String baseUrl
  ) {
    return repo -> new Repo(repo.getHttp_url_to_repo(), hasComponentMetadataFile(accessToken, baseUrl, repo));
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
    String uriWithQueryParams = uri + "?page=" + page + "&per_page=" + config.getProjectPageSize();
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
