package tech.kronicle.plugins.gitlab.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;

@Value
public class GitLabAccessTokenConfig {

  @NotEmpty
  String value;
}
