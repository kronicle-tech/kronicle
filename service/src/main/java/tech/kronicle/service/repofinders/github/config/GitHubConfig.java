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
@ConfigurationProperties("github")
@ConstructorBinding
@Value
@NonFinal
public class GitHubConfig {

  List<GitHubUser> users;
  @NotNull
  Duration timeout;
}
