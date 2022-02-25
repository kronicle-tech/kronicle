package tech.kronicle.plugins.git.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@Value
@NonFinal
public class GitConfig {

    @NotEmpty
    String reposDir;
    List<GitHost> hosts;
}
