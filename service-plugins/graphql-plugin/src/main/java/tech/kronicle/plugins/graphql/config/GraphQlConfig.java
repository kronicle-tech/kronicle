package tech.kronicle.plugins.graphql.config;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Value
public class GraphQlConfig {

    @NotNull
    Duration timeout;
}
