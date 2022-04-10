package tech.kronicle.plugins.github.models.api;

import lombok.Value;

@Value
public class GitHubRepo {

  String clone_url;
  String description;
  String contents_url;
  String statuses_url;
}
