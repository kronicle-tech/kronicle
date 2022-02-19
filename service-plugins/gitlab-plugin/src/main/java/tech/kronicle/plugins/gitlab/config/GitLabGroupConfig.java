package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

@Validated
@Value
@NonFinal
public class GitLabGroupConfig {

    String path;
    GitLabAccessTokenConfig accessToken;
}
