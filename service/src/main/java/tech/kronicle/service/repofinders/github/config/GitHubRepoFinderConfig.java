package tech.kronicle.service.repofinders.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties("repo-finders.github")
@ConstructorBinding
@Value
@NonFinal
public class GitHubRepoFinderConfig {

  List<GitHubRepoFinderUserConfig> users;
  @NotNull
  Duration timeout;
}
