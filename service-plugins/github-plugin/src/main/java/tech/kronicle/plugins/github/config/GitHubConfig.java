package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@Value
@NonFinal
public class GitHubConfig {

  String apiBaseUrl;
  List<GitHubAccessTokenConfig> accessTokens;
  List<GitHubUserConfig> users;
  List<GitHubOrganizationConfig> organizations;
  @NotNull
  Duration timeout;
}
