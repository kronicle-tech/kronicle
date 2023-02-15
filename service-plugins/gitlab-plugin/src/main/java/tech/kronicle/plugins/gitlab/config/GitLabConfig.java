package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
public class GitLabConfig {

  List<GitLabHostConfig> hosts;
  @NotNull
  Integer projectPageSize;
  @NotEmpty
  String environmentId;
  @NotNull
  Duration timeout;
  @NotNull
  Duration reposCacheTtl;
}
