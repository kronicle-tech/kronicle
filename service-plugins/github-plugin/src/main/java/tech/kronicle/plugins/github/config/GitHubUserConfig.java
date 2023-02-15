package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;

@Value
public class GitHubUserConfig {

    @NotEmpty
    String accountName;
    GitHubAccessTokenConfig accessToken;
}
