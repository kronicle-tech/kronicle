package tech.kronicle.plugins.gitlab.config;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
@NonFinal
public class GitLabConfig {

  List<GitLabHostConfig> hosts;
  @NonNull
  Integer projectPageSize;
  @NotNull
  Duration timeout;
}
