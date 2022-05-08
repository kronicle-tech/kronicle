package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Value
public class GitLabHostConfig {

    @NotNull
    @Pattern(regexp = "https://.+[^/]")
    String baseUrl;
    List<GitLabAccessTokenConfig> accessTokens;
    List<GitLabUserConfig> users;
    List<GitLabGroupConfig> groups;
}
