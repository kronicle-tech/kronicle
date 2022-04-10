package tech.kronicle.plugins.github.models.api;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GitHubStatus {

  String url;
  String avatar_url;
  String state;
  String description;
  String context;
  LocalDateTime created_at;
  LocalDateTime updated_at;
}
