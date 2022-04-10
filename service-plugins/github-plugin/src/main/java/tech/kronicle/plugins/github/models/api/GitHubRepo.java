package tech.kronicle.plugins.github.models.api;

import lombok.Value;

@Value
public class GitHubRepo {

  String name;
  String description;
  String clone_url;
  String contents_url;
  String default_branch;
  GitHubRepoOwner owner;
}
