package tech.kronicle.service.repofinders.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class GitLabRepoFinderGroupConfig {

    String path;
    GitLabRepoFinderAccessTokenConfig accessToken;
}
