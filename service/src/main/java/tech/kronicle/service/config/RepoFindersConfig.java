package tech.kronicle.service.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("repo-finders")
@ConstructorBinding
@Value
public class RepoFindersConfig {

    List<RepoFindersIgnoredRepoConfig> ignoredRepos;
}
