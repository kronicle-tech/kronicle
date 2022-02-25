package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Value
@NonFinal
public class GitLabAccessTokenConfig {

  @NotEmpty
  String value;
}
