package tech.kronicle.service.repofinders.gitlab.config;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties("repo-finders.gitlab")
@ConstructorBinding
@Value
@NonFinal
public class GitLabRepoFinderConfig {

  List<GitLabRepoFinderHostConfig> hosts;
  @NonNull
  Integer projectPageSize;
  @NotNull
  Duration timeout;
}
