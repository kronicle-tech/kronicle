package tech.kronicle.plugins.bitbucketserver.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Value
public class BitbucketServerConfig {

    List<BitbucketServerHostConfig> hosts;
    @NotNull
    Duration timeout;
}
