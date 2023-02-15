package tech.kronicle.plugins.bitbucketserver.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Value
public class BitbucketServerHostConfig {

    @NotNull
    @Pattern(regexp = "https://.+[^/]")
    String baseUrl;
    @NotEmpty
    String username;
    @NotEmpty
    String password;
}
