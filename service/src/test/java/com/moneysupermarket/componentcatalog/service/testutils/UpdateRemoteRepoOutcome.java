package com.moneysupermarket.componentcatalog.service.testutils;

import lombok.Value;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Value
public class UpdateRemoteRepoOutcome {

    Path newFile;
    String newFileCommitHash;
    LocalDateTime beforeCommit;
    LocalDateTime afterCommit;
}
