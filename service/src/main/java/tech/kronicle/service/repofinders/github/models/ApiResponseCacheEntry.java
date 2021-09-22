package tech.kronicle.service.repofinders.github.models;

import lombok.Value;

@Value
public class ApiResponseCacheEntry<T> {

  String eTag;
  T responseBody;
}
