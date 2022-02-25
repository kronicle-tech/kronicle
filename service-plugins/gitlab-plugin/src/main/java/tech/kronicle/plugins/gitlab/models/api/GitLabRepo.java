package tech.kronicle.plugins.gitlab.models.api;

import lombok.Value;

@Value
public class GitLabRepo {

  Long id;
  String default_branch;
  String http_url_to_repo;
}
