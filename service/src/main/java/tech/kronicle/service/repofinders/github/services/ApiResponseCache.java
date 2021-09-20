package tech.kronicle.service.repofinders.github.services;

import lombok.Value;
import org.springframework.stereotype.Service;
import tech.kronicle.service.repofinders.github.models.ApiResponseCacheEntry;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiResponseCache {

  private final Map<Key, ApiResponseCacheEntry<?>> cache = new HashMap<>();

  public <T> ApiResponseCacheEntry<T> getEntry(String username, String uri) {
    return (ApiResponseCacheEntry<T>) cache.get(new Key(username, uri));
  }

  public void putEntry(String username, String uri, ApiResponseCacheEntry<?> entry) {
    cache.put(new Key(username, uri), entry);
  }

  @Value
  private static class Key {

    String username;
    String uri;
  }
}
