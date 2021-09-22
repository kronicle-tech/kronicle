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
import tech.kronicle.service.repofinders.github.config.GitHubConfig;
import tech.kronicle.service.repofinders.github.config.GitHubUser;
import tech.kronicle.service.repofinders.github.constants.GitHubApiBaseUrls;
import tech.kronicle.service.repofinders.github.constants.GitHubApiHeaders;
import tech.kronicle.service.repofinders.github.constants.GitHubApiPaths;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;
import tech.kronicle.service.repofinders.github.models.api.ContentEntry;
import tech.kronicle.service.repofinders.github.models.api.UserRepo;
import tech.kronicle.service.repofinders.github.services.ApiResponseCache;
import tech.kronicle.service.services.UriVariablesBuilder;
import tech.kronicle.service.spring.stereotypes.Client;

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

  private static final List<HttpStatus> EXPECTED_STATUS_CODES = List.of(HttpStatus.OK, HttpStatus.NOT_MODIFIED);

  private final WebClient webClient;
  private final GitHubConfig config;
  private final ApiResponseCache cache;
  private final String gitHubApiBaseUrl;

  public GitHubClient(WebClient webClient, GitHubConfig config, ApiResponseCache cache,
                      @Value(GitHubApiBaseUrls.API_DOT_GITHUB_DOT_COM) String gitHubApiBaseUrl) {
    this.webClient = webClient;
    this.config = config;
    this.cache = cache;
    this.gitHubApiBaseUrl = gitHubApiBaseUrl;
  }

  public List<ApiRepo> getRepos(GitHubUser user) {
    return getUserRepos(user).stream()
            .map(addHasComponentMetadataFile(user))
            .collect(Collectors.toList());
  }

  private List<UserRepo> getUserRepos(GitHubUser user) {
    String uri = gitHubApiBaseUrl + GitHubApiPaths.USER_REPOS;
    return getResource(user, uri, new ParameterizedTypeReference<>() {});
  }

  private Function<UserRepo, ApiRepo> addHasComponentMetadataFile(GitHubUser user) {
    return userRepo -> new ApiRepo(userRepo.getClone_url(), hasComponentMetadataFile(user, userRepo));
  }

  private boolean hasComponentMetadataFile(GitHubUser user, UserRepo userRepo) {
    String uriTemplate = userRepo.getContents_url();
    Map<String, String> uriVariables = UriVariablesBuilder.builder()
            .addUriVariable("+path", "")
            .build();
    List<ContentEntry> contentEntries = getResource(user, expandUriTemplate(uriTemplate, uriVariables),
            new ParameterizedTypeReference<>() {});
    return contentEntries.stream()
            .anyMatch(contentEntry -> KronicleMetadataFilePaths.ALL.contains(contentEntry.getName()));
  }

  private <T> T getResource(GitHubUser user, String uri, ParameterizedTypeReference<T> responseBodyTypeRef) {
    logWebCall(user, uri);
    ApiResponseCacheEntry<T> cacheEntry = cache.getEntry(user.getUsername(), uri);
    ClientResponse response = makeRequest(user, webClient.get().uri(uri), cacheEntry);
    checkResponseStatus(response, uri);
    logRateLimitDetails(user, uri, response);
    if (Objects.equals(response.statusCode(), HttpStatus.NOT_MODIFIED)) {
      logNotModifiedResponse(user, uri);
      return cacheEntry.getResponseBody();
    }
    logWasModifiedResponse(user, uri);
    T responseBody = response
            .bodyToMono(responseBodyTypeRef)
            .block(config.getTimeout());
    cache.putEntry(user.getUsername(), uri, createCacheEntry(response, responseBody));
    return responseBody;
  }

  private void logRateLimitDetails(GitHubUser user, String uri, ClientResponse response) {
    if (log.isInfoEnabled()) {
      log.info("Request limits after call {} for user {}: rate limit {}, remaining {}, reset {}, used {}, resource {}",
              uri,
              user.getUsername(),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_LIMIT),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_REMAINING),
              formatRateLimitResetTimestamp(getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESET)),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_USED),
              getResponseHeader(response, GitHubApiHeaders.RATE_LIMIT_RESOURCE));
    }
  }

  private void logNotModifiedResponse(GitHubUser user, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Response for {} for user {} was same as last call", uri, user.getUsername());
    }
  }

  private void logWasModifiedResponse(GitHubUser user, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Response for {} for user {} was different to last call", uri, user.getUsername());
    }
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

  private void logWebCall(GitHubUser user, String uri) {
    if (log.isInfoEnabled()) {
      log.info("Calling {} for user {}", uri, user.getUsername());
    }
  }

  private ClientResponse makeRequest(GitHubUser user, WebClient.RequestHeadersSpec<?> requestHeadersSpec,
                                     ApiResponseCacheEntry<?> cacheEntry) {
    return requestHeadersSpec
            .headers(headers -> {
              headers.setBasicAuth(user.getUsername(), user.getPersonalAccessToken());
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
