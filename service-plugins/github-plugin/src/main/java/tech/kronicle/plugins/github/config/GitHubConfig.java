package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class GitHubConfig {

  String apiBaseUrl;
  List<GitHubAccessTokenConfig> personalAccessTokens;
  List<GitHubUserConfig> users;
  List<GitHubOrganizationConfig> organizations;
  @NotNull
  Duration timeout;
}
