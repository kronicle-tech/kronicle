package tech.kronicle.service.repofinders.github.models.api;

import lombok.Value;

@Value
public class GitHubRepo {

  String clone_url;
  String contents_url;
}
