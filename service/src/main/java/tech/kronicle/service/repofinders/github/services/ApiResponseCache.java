package tech.kronicle.service.repofinders.github.services;

import lombok.Value;
import org.springframework.stereotype.Service;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderAccessTokenConfig;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
public class ApiResponseCache {

  private final Map<Key, ApiResponseCacheEntry<?>> cache = new HashMap<>();

  public <T> ApiResponseCacheEntry<T> getEntry(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
    return (ApiResponseCacheEntry<T>) cache.get(Key.create(accessToken, uri));
  }

  public void putEntry(GitHubRepoFinderAccessTokenConfig accessToken, String uri, ApiResponseCacheEntry<?> entry) {
    cache.put(Key.create(accessToken, uri), entry);
  }

  @Value
  private static class Key {

    String username;
    String uri;

    static Key create(GitHubRepoFinderAccessTokenConfig accessToken, String uri) {
      return nonNull(accessToken)
              ? new Key(accessToken.getUsername(), uri)
              : new Key(null, uri);
    }
  }
}
