package tech.kronicle.plugins.bitbucketserver.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Validated
@Value
@NonFinal
public class BitbucketServerHostConfig {

    @NotNull
    @Pattern(regexp = "https://.+[^/]")
    String baseUrl;
    @NotEmpty
    String username;
    @NotEmpty
    String password;
}
