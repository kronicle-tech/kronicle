package tech.kronicle.service.repofinders.bitbucketserver.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties("repo-finders.bitbucket-server")
@ConstructorBinding
@Value
@NonFinal
public class BitbucketServerRepoFinderConfig {

    List<BitbucketServerRepoFinderHostConfig> hosts;
    @NotNull
    Duration timeout;
}
