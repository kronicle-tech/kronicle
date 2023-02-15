package tech.kronicle.service.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("repo-finders")
@Value
public class RepoFindersConfig {

    List<RepoFindersIgnoredRepoConfig> ignoredRepos;
}
