package tech.kronicle.plugins.gitlab.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.config.GitLabGroupConfig;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabUserConfig;
import tech.kronicle.plugins.gitlab.constants.GitLabApiHeaders;
import tech.kronicle.plugins.gitlab.constants.GitLabApiPaths;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;
import tech.kronicle.pluginutils.services.UriVariablesBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.pluginutils.utils.UriTemplateUtils.expandUriTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class GitLabClient {

  private final WebClient webClient;
  private final GitLabConfig config;

  public List<ApiRepo> getRepos(String baseUrl, GitLabAccessTokenConfig accessToken) {
    return getRepos(accessToken, baseUrl, getAllProjectsUri());
  }

  public List<ApiRepo> getRepos(String baseUrl, GitLabUserConfig user) {
    return getRepos(user.getAccessToken(), baseUrl, getUserProjectsUri(user));
  }

  public List<ApiRepo> getRepos(String baseUrl, GitLabGroupConfig group) {
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

  private List<ApiRepo> getRepos(GitLabAccessTokenConfig accessToken, String baseUrl, String uri) {
    return getAllPagedResources(accessToken, baseUrl + uri, new ParameterizedTypeReference<List<GitLabRepo>>() {})
            .map(addHasComponentMetadataFile(accessToken, baseUrl))
            .collect(Collectors.toList());
  }

  private <T> Stream<T> getAllPagedResources(
          GitLabAccessTokenConfig accessToken,
          String uri,
          ParameterizedTypeReference<List<T>> bodyTypeReference
  ) {
    return Stream.iterate(
            getPagedResource(accessToken, uri, 1, bodyTypeReference),
            Objects::nonNull,
            page -> getPagedResource(accessToken, uri, page.nextPage, bodyTypeReference)
    )
            .flatMap(page -> page.items.stream());
  }

  private Function<GitLabRepo, ApiRepo> addHasComponentMetadataFile(
          GitLabAccessTokenConfig accessToken,
          String baseUrl
  ) {
    return repo -> new ApiRepo(repo.getHttp_url_to_repo(), hasComponentMetadataFile(accessToken, baseUrl, repo));
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

  private <T> PagedResource<T> getPagedResource(
          GitLabAccessTokenConfig accessToken,
          String uri,
          Integer page,
          ParameterizedTypeReference<List<T>> bodyTypeReference
  ) {
    if (isNull(page)) {
      return null;
    }
    String uriWithQueryParams = uri + "?page=" + page + "&per_page=" + config.getProjectPageSize();
    logWebCall(uriWithQueryParams);
    ResponseEntity<List<T>> responseEntity = makeRequest(
            accessToken, webClient.get().uri(uriWithQueryParams)
    )
            .toEntity(bodyTypeReference)
            .block(config.getTimeout());
    return new PagedResource<>(responseEntity);
  }

  private boolean doesResourceExist(GitLabAccessTokenConfig accessToken, String uri) {
    logWebCall(uri);
    boolean exists;
    try {
      makeRequest(accessToken, webClient.get().uri(uri))
              .toBodilessEntity()
              .block(config.getTimeout());
      exists = true;
    } catch (WebClientResponseException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        exists = false;
      } else {
        throw ex;
      }
    }
    log.info("Resource exists: {}", exists);
    return exists;
  }

  private void logWebCall(String uri) {
    log.info("Calling {}", uri);
  }

  private WebClient.ResponseSpec makeRequest(GitLabAccessTokenConfig accessToken, WebClient.RequestHeadersSpec<?> requestHeadersSpec) {
    if (nonNull(accessToken)) {
      requestHeadersSpec
              .headers(headers -> headers.add(GitLabApiHeaders.PRIVATE_TOKEN, accessToken.getValue()));
    }
    return requestHeadersSpec.retrieve();
  }

  private static class PagedResource<T> {

    private final List<T> items;
    private final Integer nextPage;

    public PagedResource(ResponseEntity<List<T>> responseEntity) {
      items = responseEntity.getBody();
      nextPage = getNextPage(responseEntity);
    }

    private Integer getNextPage(ResponseEntity<List<T>> responseEntity) {
      return getOptionalNextPage(responseEntity).map(Integer::parseInt).orElse(null);
    }

    private Optional<String> getOptionalNextPage(ResponseEntity<List<T>> responseEntity) {
      return Optional.ofNullable(responseEntity.getHeaders()
              .getFirst(GitLabApiHeaders.X_NEXT_PAGE))
              .filter(value -> !value.isEmpty());
    }

    public boolean isNotEmpty() {
      return !items.isEmpty();
    }
  }
}
