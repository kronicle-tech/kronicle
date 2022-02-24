package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class GitLabUserConfig {

    String username;
    GitLabAccessTokenConfig accessToken;
}
