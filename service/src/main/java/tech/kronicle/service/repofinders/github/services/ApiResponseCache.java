package tech.kronicle.service.repofinders.github.services;

import lombok.Value;
import org.springframework.stereotype.Service;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderPersonalAccessTokenConfig;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
public class ApiResponseCache {

  private final Map<Key, ApiResponseCacheEntry<?>> cache = new HashMap<>();

  public <T> ApiResponseCacheEntry<T> getEntry(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
    return (ApiResponseCacheEntry<T>) cache.get(Key.create(personalAccessToken, uri));
  }

  public void putEntry(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri, ApiResponseCacheEntry<?> entry) {
    cache.put(Key.create(personalAccessToken, uri), entry);
  }

  @Value
  private static class Key {

    String username;
    String uri;

    static Key create(GitHubRepoFinderPersonalAccessTokenConfig personalAccessToken, String uri) {
      return nonNull(personalAccessToken)
              ? new Key(personalAccessToken.getUsername(), uri)
              : new Key(null, uri);
    }
  }
}
