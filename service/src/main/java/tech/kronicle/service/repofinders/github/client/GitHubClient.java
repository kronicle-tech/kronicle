package tech.kronicle.service.repofinders.github.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.service.constants.KronicleMetadataFilePaths;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderOrganizationConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderAccessTokenConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderUserConfig;
import tech.kronicle.service.repofinders.github.constants.GitHubApiBaseUrls;
import tech.kronicle.service.repofinders.github.constants.GitHubApiHeaders;
import tech.kronicle.service.repofinders.github.constants.GitHubApiPaths;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubContentEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubRepo;
import tech.kronicle.service.repofinders.github.services.ApiResponseCache;
import tech.kronicle.service.services.UriVariablesBuilder;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.service.utils.UriTemplateUtils.expandUriTemplate;

@SpringComponent
@Slf4j
public class GitHubClient {

  private static final List<HttpStatus> EXPECTED_STATUS_CODES = List.of(HttpStatus.OK, HttpStatus.NOT_MODIFIED, HttpStatus.NOT_FOUND);

  private final WebClient webClient;
  private final GitHubRepoFinderConfig config;
  private final ApiResponseCache cache;
  private final String gitHubApiBaseUrl;

  public GitHubClient(WebClient webClient, GitHubRepoFinderConfig config, ApiResponseCache cache,
                      @Value(GitHubApiBaseUrls.API_DOT_GITHUB_DOT_COM) String gitHubApiBaseUrl) {
    this.webClient = webClient;
    this.config = config;
    this.cache = cache;
    this.gitHubApiBaseUrl = gitHubApiBaseUrl;
  }

  public List<ApiRepo> getRepos(GitHubRepoFinderAccessTokenConfig accessToken) {
    return getRepos(accessToken, getAuthenticatedUserReposUri());
  }

  public List<ApiRepo> getRepos(GitHubRepoFinderUserConfig user) {
    return getRepos(user.getAccessToken(), getUserReposUri(user));
  }

  public List<ApiRepo> getRepos(GitHubRepoFinderOrganizationConfig organization) {
    return getRepos(organization.getAccessToken(), getOrganizationReposUri(organization));
  }

  private String getAuthenticatedUserReposUri() {
    return gitHubApiBaseUrl + GitHubApiPaths.AUTHENTICATED_USER_REPOS;
  }

  private String getUserReposUri(GitHubRepoFinderUserConfig user) {
    return expandUriTemplate(gitHubApiBaseUrl + GitHubApiPaths.USER_REPOS, Map.of("username", user.getAccountName()));
  }

  private String getOrganizationReposUri(GitHubRepoFinderOrganizationConfig organization) {
    return expandUriTemplate(gitHubApiBaseUrl + GitHubApiPaths.ORGANIZATION_REPOS, Map.of("org", organization.getAccountName()));
  }

  private List<ApiRepo> getRepos(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    List<GitHubRepo> repos = getGitHubRepos(accessToken, uri);
    if (isNull(repos)) {
      return List.of();
    }
    return repos.stream()
            .map(addHasComponentMetadataFile(accessToken))
            .collect(Collectors.toList());
  }

  private List<GitHubRepo> getGitHubRepos(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    return getResource(accessToken, uri, new ParameterizedTypeReference<>() {});
  }

  private Function<GitHubRepo, ApiRepo> addHasComponentMetadataFile(GitHubRepoFinderAccessTokenConfig accessToken) {
    return gitHubRepo -> new ApiRepo(gitHubRepo.getClone_url(), hasComponentMetadataFile(accessToken, gitHubRepo));
  }

  private boolean hasComponentMetadataFile(GitHubRepoFinderAccessTokenConfig accessToken, GitHubRepo repo) {
    String uriTemplate = repo.getContents_url();
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("+path", "")
            .build();
    List<GitHubContentEntry> contentEntries = getResource(accessToken, expandUriTemplate(uriTemplate, uriVariables),
            new ParameterizedTypeReference<>() {});
    if (isNull(contentEntries)) {
      return false;
    }
    return contentEntries.stream()
            .anyMatch(contentEntry -> KronicleMetadataFilePaths.ALL.contains(contentEntry.getName()));
  }

  private <T> T getResource(GitHubRepoFinderAccessTokenConfig accessToken, String uri, ParameterizedTypeReference<T> responseBodyTypeRef) {
    logWebCall(accessToken, uri);
    ApiResponseCacheEntry<T> cacheEntry = cache.getEntry(accessToken, uri);
    ClientResponse response = makeRequest(accessToken, webClient.get().uri(uri), cacheEntry);
    checkResponseStatus(response, uri);
    logRateLimitDetails(accessToken, uri, response);
    if (Objects.equals(response.statusCode(), HttpStatus.NOT_MODIFIED)) {
      logNotModifiedResponse(accessToken, uri);
      return cacheEntry.getResponseBody();
    } else if (Objects.equals(response.statusCode(), HttpStatus.NOT_FOUND)) {
      logNotFoundResponse(accessToken, uri);
      return null;
    } else {
      logWasModifiedResponse(accessToken, uri);
      T responseBody = response
              .bodyToMono(responseBodyTypeRef)
              .block(config.getTimeout());
      cache.putEntry(accessToken, uri, createCacheEntry(response, responseBody));
      return responseBody;
    }
  }

  private void logRateLimitDetails(GitHubRepoFinderAccessTokenConfig accessToken, String uri, ClientResponse response) {
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

  private void logNotModifiedResponse(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    log.info("Response for {} for user {} was same as last call", uri, getUsername(accessToken));
  }

  private void logNotFoundResponse(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    log.info("Not found response for {} for user {}", uri, getUsername(accessToken));
  }

  private void logWasModifiedResponse(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    log.info("Response for {} for user {} was different to last call", uri, getUsername(accessToken));
  }

  @NotEmpty
  private String getUsername(GitHubRepoFinderAccessTokenConfig accessToken) {
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

  private <T> ApiResponseCacheEntry<T> createCacheEntry(ClientResponse clientResponse, T responseBody) {
    return new ApiResponseCacheEntry<>(getETagFromResponse(clientResponse), responseBody);
  }

  private String getETagFromResponse(ClientResponse response) {
    return getResponseHeader(response, HttpHeaders.ETAG);
  }

  private String getResponseHeader(ClientResponse response, String headerName) {
    List<String> headerValues = response.headers().header(headerName);
    return headerValues.isEmpty() ? null : headerValues.get(0);
  }

  private void logWebCall(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {} for user {}", uri, getUsername(accessToken));
    }
  }

  private ClientResponse makeRequest(GitHubRepoFinderAccessTokenConfig accessToken, WebClient.RequestHeadersSpec<?> requestHeadersSpec,
                                     ApiResponseCacheEntry<?> cacheEntry) {
    return requestHeadersSpec
            .headers(headers -> {
              if (nonNull(accessToken)) {
                headers.setBasicAuth(accessToken.getUsername(), accessToken.getValue());
              }
              if (nonNull(cacheEntry)) {
                headers.add(HttpHeaders.IF_NONE_MATCH, cacheEntry.getETag());
              }
            })
            .exchange()
            .block(config.getTimeout());
  }

  private void checkResponseStatus(ClientResponse clientResponse, String uri) {
    if (!EXPECTED_STATUS_CODES.contains(clientResponse.statusCode())) {
      String responseBody = clientResponse.bodyToMono(String.class).block(config.getTimeout());

      GitHubClientException exception = new GitHubClientException(uri, clientResponse.rawStatusCode(),
              responseBody);
      log.warn(exception.getMessage());
      throw exception;
    }
  }
}
