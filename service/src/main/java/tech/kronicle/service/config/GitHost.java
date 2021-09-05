package tech.kronicle.service.config;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class GitHost {

    @NotEmpty
    String host;
    String username;
    String password;
}
