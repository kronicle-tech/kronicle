package tech.kronicle.service.repofinders.gitlab.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tech.kronicle.service.constants.KronicleMetadataFilePaths;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderAccessTokenConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderGroupConfig;
import tech.kronicle.service.repofinders.gitlab.config.GitLabRepoFinderUserConfig;
import tech.kronicle.service.repofinders.gitlab.constants.GitLabApiHeaders;
import tech.kronicle.service.repofinders.gitlab.constants.GitLabApiPaths;
import tech.kronicle.service.repofinders.gitlab.models.api.GitLabRepo;
import tech.kronicle.service.services.UriVariablesBuilder;
import tech.kronicle.service.spring.stereotypes.Client;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static tech.kronicle.service.utils.UriTemplateUtils.expandUriTemplate;

@Client
@Slf4j
public class GitLabClient {

  private final WebClient webClient;
  private final GitLabRepoFinderConfig config;

  public GitLabClient(WebClient webClient, GitLabRepoFinderConfig config) {
    this.webClient = webClient;
    this.config = config;
  }

  public List<ApiRepo> getRepos(String baseUrl, GitLabRepoFinderAccessTokenConfig accessToken) {
    return getRepos(accessToken, baseUrl, getAllProjectsUri());
  }

  public List<ApiRepo> getRepos(String baseUrl, GitLabRepoFinderUserConfig user) {
    return getRepos(user.getAccessToken(), baseUrl, getUserProjectsUri(user));
  }

  public List<ApiRepo> getRepos(String baseUrl, GitLabRepoFinderGroupConfig group) {
    return getRepos(group.getAccessToken(), baseUrl, getGroupProjectsUri(group));
  }

  private String getAllProjectsUri() {
    return GitLabApiPaths.PROJECTS;
  }

  private String getUserProjectsUri(GitLabRepoFinderUserConfig user) {
    return expandUriTemplate(GitLabApiPaths.USER_PROJECTS, Map.of("username", user.getUsername()));
  }

  private String getGroupProjectsUri(GitLabRepoFinderGroupConfig group) {
    return expandUriTemplate(GitLabApiPaths.GROUP_PROJECTS, Map.of("groupPath", group.getPath()));
  }

  private List<ApiRepo> getRepos(GitLabRepoFinderAccessTokenConfig accessToken, String baseUrl, String uri) {
    return getAllPagedResources(accessToken, baseUrl + uri, new ParameterizedTypeReference<List<GitLabRepo>>() {})
            .map(addHasComponentMetadataFile(accessToken, baseUrl))
            .collect(Collectors.toList());
  }

  private <T> Stream<T> getAllPagedResources(
          GitLabRepoFinderAccessTokenConfig accessToken,
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
          GitLabRepoFinderAccessTokenConfig accessToken,
          String baseUrl
  ) {
    return repo -> new ApiRepo(repo.getHttp_url_to_repo(), hasComponentMetadataFile(accessToken, baseUrl, repo));
  }

  private boolean hasComponentMetadataFile(
          GitLabRepoFinderAccessTokenConfig accessToken,
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

  private <T> PagedResource<T> getPagedResource(
          GitLabRepoFinderAccessTokenConfig accessToken,
          String uri,
          Integer page,
          ParameterizedTypeReference<List<T>> bodyTypeReference
  ) {
    if (isNull(page)) {
      return null;
    }
    String uriWithQueryParams = uri + "?page=" + page + "&per_page=" + config.getProjectPageSize();
    logWebCall(uri);
    ResponseEntity<List<T>> responseEntity = makeRequest(
            accessToken, webClient.get().uri(uriWithQueryParams)
    )
            .toEntity(bodyTypeReference)
            .block(config.getTimeout());
    return new PagedResource<>(responseEntity);
  }

  private boolean doesResourceExist(GitLabRepoFinderAccessTokenConfig accessToken, String uri) {
    logWebCall(uri);
    return makeRequest(accessToken, webClient.head().uri(uri))
            .toBodilessEntity()
            .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode() == HttpStatus.NOT_FOUND ? Mono.empty() : Mono.error(ex))
            .map(responseEntity -> responseEntity.getStatusCode() == HttpStatus.OK)
            .switchIfEmpty(Mono.just(false))
            .block(config.getTimeout());
  }

  private void logWebCall(String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {}", uri);
    }
  }

  private WebClient.ResponseSpec makeRequest(GitLabRepoFinderAccessTokenConfig accessToken, WebClient.RequestHeadersSpec<?> requestHeadersSpec) {
    return requestHeadersSpec
            .headers(headers -> headers.add(GitLabApiHeaders.PRIVATE_TOKEN, accessToken.getValue()))
            .retrieve();
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
      return Optional.ofNullable(responseEntity.getHeaders().getFirst(GitLabApiHeaders.X_NEXT_PAGE));
    }

    public boolean isNotEmpty() {
      return !items.isEmpty();
    }
  }
}