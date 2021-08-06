package com.moneysupermarket.componentcatalog.service.testutils;

import lombok.Value;

import java.nio.file.Path;

@Value
public class CreateBranchInRemoteRepoOutcome {

    String newBranchName;
    Path newFile;
    String newFileCommitHash;
}
