package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

@Validated
@Value
@NonFinal
public class GitHubOrganizationConfig {

    String accountName;
    GitHubAccessTokenConfig accessToken;
}
