package tech.kronicle.service.config;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@Value
public class RepoFindersIgnoredRepoConfig {

    @NotBlank
    String url;
}
