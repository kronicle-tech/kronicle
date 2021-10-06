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
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderUserConfig;
import tech.kronicle.service.repofinders.github.constants.GitHubApiBaseUrls;
import tech.kronicle.service.repofinders.github.constants.GitHubApiHeaders;
import tech.kronicle.service.repofinders.github.constants.GitHubApiPaths;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubContentEntry;
import tech.kronicle.service.repofinders.github.models.api.GitHubRepo;
import tech.kronicle.service.repofinders.github.services.ApiResponseCache;
import tech.kronicle.service.services.UriVariablesBuilder;
import tech.kronicle.service.spring.stereotypes.Client;

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

@Client
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

  public List<ApiRepo> getRepos(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken) {
    return getRepos(personalAccessToken, getAuthenticatedUserReposUri());
  }

  public List<ApiRepo> getRepos(GitHubRepoFinderUserConfig user) {
    return getRepos(null, getUserReposUri(user));
  }

  public List<ApiRepo> getRepos(GitHubRepoFinderOrganizationConfig organization) {
    return getRepos(null, getOrganizationReposUri(organization));
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

  private List<ApiRepo> getRepos(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    return getGitHubRepos(personalAccessToken, uri).stream()
            .map(addHasComponentMetadataFile(personalAccessToken))
            .collect(Collectors.toList());
  }

  private List<GitHubRepo> getGitHubRepos(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    return getResource(personalAccessToken, uri, new ParameterizedTypeReference<>() {});
  }

  private Function<GitHubRepo, ApiRepo> addHasComponentMetadataFile(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken) {
    return gitHubRepo -> new ApiRepo(gitHubRepo.getClone_url(), hasComponentMetadataFile(personalAccessToken, gitHubRepo));
  }

  private boolean hasComponentMetadataFile(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, GitHubRepo repo) {
    String uriTemplate = repo.getContents_url();
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("+path", "")
            .build();
    List<GitHubContentEntry> contentEntries = getResource(personalAccessToken, expandUriTemplate(uriTemplate, uriVariables),
            new ParameterizedTypeReference<>() {});
    if (isNull(contentEntries)) {
      return false;
    }
    return contentEntries.stream()
            .anyMatch(contentEntry -> KronicleMetadataFilePaths.ALL.contains(contentEntry.getName()));
  }

  private <T> T getResource(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri, ParameterizedTypeReference<T> responseBodyTypeRef) {
    logWebCall(personalAccessToken, uri);
    ApiResponseCacheEntry<T> cacheEntry = cache.getEntry(personalAccessToken, uri);
    ClientResponse response = makeRequest(personalAccessToken, webClient.get().uri(uri), cacheEntry);
    checkResponseStatus(response, uri);
    logRateLimitDetails(personalAccessToken, uri, response);
    if (Objects.equals(response.statusCode(), HttpStatus.NOT_MODIFIED)) {
      logNotModifiedResponse(personalAccessToken, uri);
      return cacheEntry.getResponseBody();
    } else if (Objects.equals(response.statusCode(), HttpStatus.NOT_FOUND)) {
      logNotFoundResponse(personalAccessToken, uri);
      return null;
    } else {
      logWasModifiedResponse(personalAccessToken, uri);
      T responseBody = response
              .bodyToMono(responseBodyTypeRef)
              .block(config.getTimeout());
      cache.putEntry(personalAccessToken, uri, createCacheEntry(response, responseBody));
      return responseBody;
    }
  }

  private void logRateLimitDetails(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri, ClientResponse response) {
    if (log.isInfoEnabled()) {
      log.info("Request limits after call {} for user {}: rate limit {}, remaining {}, reset {}, used {}, resource {}",
              uri,
              getUsername(personalAccessToken),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_LIMIT),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_REMAINING),
              formatRateLimitResetTimestamp(getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESET)),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_USED),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESOURCE));
    }
  }

  private void logNotModifiedResponse(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    log.info("Not found response for {} for user {}", uri, getUsername(personalAccessToken));
  }

  private void logNotFoundResponse(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    log.info("Response for {} for user {} was same as last call", uri, getUsername(personalAccessToken));
  }

  private void logWasModifiedResponse(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    log.info("Response for {} for user {} was different to last call", uri, getUsername(personalAccessToken));
  }

  @NotEmpty
  private String getUsername(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken) {
    return nonNull(personalAccessToken) ? personalAccessToken.getUsername() : "anonymous";
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

  private void logWebCall(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {} for user {}", uri, getUsername(personalAccessToken));
    }
  }

  private ClientResponse makeRequest(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, WebClient.RequestHeadersSpec<?> requestHeadersSpec,
                                     ApiResponseCacheEntry<?> cacheEntry) {
    return requestHeadersSpec
            .headers(headers -> {
              if (nonNull(personalAccessToken)) {
                headers.setBasicAuth(personalAccessToken.getUsername(), personalAccessToken.getPersonalAccessToken());
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
