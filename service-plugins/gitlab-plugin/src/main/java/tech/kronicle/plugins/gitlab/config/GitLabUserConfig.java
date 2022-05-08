package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public class GitLabUserConfig {

    String username;
    GitLabAccessTokenConfig accessToken;
}
