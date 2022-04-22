package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
@NonFinal
public class GitLabConfig {

  List<GitLabHostConfig> hosts;
  @NotNull
  Integer projectPageSize;
  @NotEmpty
  String environmentId;
  @NotNull
  Duration timeout;
}
