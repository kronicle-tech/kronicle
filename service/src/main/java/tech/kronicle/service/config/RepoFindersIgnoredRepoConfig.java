package tech.kronicle.service.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@ConstructorBinding
@Value
public class RepoFindersIgnoredRepoConfig {

    @NotBlank
    String url;
}
