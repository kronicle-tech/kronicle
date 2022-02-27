package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
@NonFinal
public class GitHubUserConfig {

    @NotEmpty
    String accountName;
    GitHubAccessTokenConfig accessToken;
}
