package tech.kronicle.plugins.bitbucketserver.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@Value
@NonFinal
public class BitbucketServerConfig {

    List<BitbucketServerHostConfig> hosts;
    @NotNull
    Duration timeout;
}
