package tech.kronicle.plugins.git.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
public class GitConfig {

    @NotEmpty
    String reposDir;
    List<GitHost> hosts;
}
