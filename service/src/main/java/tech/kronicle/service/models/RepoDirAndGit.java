package tech.kronicle.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.eclipse.jgit.api.Git;

import java.nio.file.Path;

@Value
@AllArgsConstructor
@Builder
public class RepoDirAndGit implements AutoCloseable {

    Path repoDir;
    Git git;

    @Override
    public void close() {
        git.close();
    }
}
