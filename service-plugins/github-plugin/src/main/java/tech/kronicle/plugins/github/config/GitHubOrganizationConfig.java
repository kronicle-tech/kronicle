package tech.kronicle.plugins.github.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Value
@NonFinal
public class GitHubOrganizationConfig {

    @NotEmpty
    String accountName;
    GitHubAccessTokenConfig accessToken;
}
