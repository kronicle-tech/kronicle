package tech.kronicle.plugins.git.config;

import lombok.Value;

import jakarta.validation.constraints.NotEmpty;

@Value
public class GitHost {

    @NotEmpty
    String host;
    String username;
    String password;
}
