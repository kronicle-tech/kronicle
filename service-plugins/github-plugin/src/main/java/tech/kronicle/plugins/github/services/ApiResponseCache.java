package tech.kronicle.plugins.github.services;

import lombok.Value;
import tech.kronicle.plugins.github.config.GitHubAccessTokenConfig;
import tech.kronicle.plugins.github.models.ApiResponseCacheEntry;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class ApiResponseCache {

  private final Map<Key, ApiResponseCacheEntry<?>> cache = new HashMap<>();

  public <T> ApiResponseCacheEntry<T> getEntry(GitHubAccessTokenConfig accessToken, String uri) {
    return (ApiResponseCacheEntry<T>) cache.get(Key.create(accessToken, uri));
  }

  public void putEntry(GitHubAccessTokenConfig accessToken, String uri, ApiResponseCacheEntry<?> entry) {
    cache.put(Key.create(accessToken, uri), entry);
  }

  @Value
  private static class Key {

    String username;
    String uri;

    static Key create(GitHubAccessTokenConfig accessToken, String uri) {
      return nonNull(accessToken)
              ? new Key(accessToken.getUsername(), uri)
              : new Key(null, uri);
    }
  }
}
