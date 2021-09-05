package tech.kronicle.service.testutils;

import lombok.Value;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Value
public class CreateRemoteRepoOutcome {

    Path repoDir;
    LocalDateTime beforeCommit;
    LocalDateTime afterCommit;
}
