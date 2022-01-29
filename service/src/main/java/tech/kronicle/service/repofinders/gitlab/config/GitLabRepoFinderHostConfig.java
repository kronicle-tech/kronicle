package tech.kronicle.service.repofinders.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class GitLabRepoFinderHostConfig {

    @NotNull
    @Pattern(regexp = "https://.+[^/]")
    String baseUrl;
    List<GitLabRepoFinderAccessTokenConfig> accessTokens;
    List<GitLabRepoFinderUserConfig> users;
    List<GitLabRepoFinderGroupConfig> groups;
}
