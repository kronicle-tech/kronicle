package tech.kronicle.plugins.github.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
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
import tech.kronicle.pluginutils.HttpStatuses;
import tech.kronicle.pluginutils.UriVariablesBuilder;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
import static tech.kronicle.pluginutils.BasicAuthUtils.basicAuth;
import static tech.kronicle.pluginutils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.pluginutils.UriTemplateUtils.expandUriTemplate;

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

  public List<ApiRepo> getRepos(GitHubAccessTokenConfig accessToken) {
    return getRepos(accessToken, getAuthenticatedUserReposUri());
  }

  public List<ApiRepo> getRepos(GitHubUserConfig user) {
    return getRepos(user.getAccessToken(), getUserReposUri(user));
  }

  public List<ApiRepo> getRepos(GitHubOrganizationConfig organization) {
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

  private List<ApiRepo> getRepos(GitHubAccessTokenConfig accessToken, String uri) {
    List<GitHubRepo> repos = getGitHubRepos(accessToken, uri);
    if (isNull(repos)) {
      return List.of();
    }
    return repos.stream()
            .map(addHasComponentMetadataFile(accessToken))
            .collect(Collectors.toList());
  }

  private List<GitHubRepo> getGitHubRepos(GitHubAccessTokenConfig accessToken, String uri) {
    return getResource(accessToken, uri, new TypeReference<>() {});
  }

  private Function<GitHubRepo, ApiRepo> addHasComponentMetadataFile(GitHubAccessTokenConfig accessToken) {
    return gitHubRepo -> new ApiRepo(gitHubRepo.getClone_url(), hasComponentMetadataFile(accessToken, gitHubRepo));
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
}
