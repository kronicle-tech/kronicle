package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
public class GitHubConfig {

  @NotEmpty
  String apiBaseUrl;
  List<GitHubAccessTokenConfig> accessTokens;
  List<GitHubUserConfig> users;
  List<GitHubOrganizationConfig> organizations;
  @NotEmpty
  String environmentId;
  @NotNull
  Duration timeout;
}
