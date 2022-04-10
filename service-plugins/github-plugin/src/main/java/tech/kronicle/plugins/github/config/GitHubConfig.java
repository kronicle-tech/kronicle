package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
@NonFinal
public class GitHubConfig {

  @NotEmpty
  String apiBaseUrl;
  List<GitHubAccessTokenConfig> accessTokens;
  List<GitHubUserConfig> users;
  List<GitHubOrganizationConfig> organizations;
  @NotNull
  Duration timeout;
}
