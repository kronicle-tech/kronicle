package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.config.GitConfig;
import com.moneysupermarket.componentcatalog.service.models.RepoDirAndGit;
import com.moneysupermarket.componentcatalog.service.testutils.CreateBranchInRemoteRepoOutcome;
import com.moneysupermarket.componentcatalog.service.testutils.CreateRemoteRepoOutcome;
import com.moneysupermarket.componentcatalog.service.testutils.GitRepoHelper;
import com.moneysupermarket.componentcatalog.service.testutils.UpdateRemoteRepoOutcome;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GitClonerTest {

    @TempDir
    public Path tempDir;
    private GitRepoHelper gitRepoHelper;
    private GitCloner underTest;

    @BeforeEach
    public void beforeEach() throws IOException {
        gitRepoHelper = new GitRepoHelper(tempDir);
    }

    @Test
    public void cloneOrPullRepoShouldCloneARepoWhenNotClonedBefore() throws IOException, GitAPIException, URISyntaxException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        try (RepoDirAndGit returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString())) {
            // Then
            assertThat(returnValue.getRepoDir()).exists();
            assertThat(returnValue.getRepoDir()).isNotEmptyDirectory();
        }
    }

    @Test
    public void cloneOrPullRepoShouldFetchARepoWhenClonedBefore() throws IOException, GitAPIException, URISyntaxException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        try (RepoDirAndGit returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString())) {
            // Then
            assertThat(returnValue.getRepoDir()).exists();
            assertThat(returnValue.getRepoDir()).isNotEmptyDirectory();
            Instant lastModifiedTime = getLastModifiedTime(returnValue);

            // When
            UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
            RepoDirAndGit returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

            // Then
            assertThat(returnValue2.getRepoDir()).isEqualTo(returnValue.getRepoDir());
            Instant lastModifiedTime2 = getLastModifiedTime(returnValue);
            assertThat(lastModifiedTime2).isAfter(lastModifiedTime);
            assertThat(updateOutcome.getNewFile()).exists();
        }
    }

    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenGitOpenFails() throws IOException, GitAPIException, URISyntaxException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        try (RepoDirAndGit returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString())) {
            // Then
            assertThat(returnValue.getRepoDir()).exists();
            assertThat(returnValue.getRepoDir()).isNotEmptyDirectory();
            Path untrackedFile = returnValue.getRepoDir().resolve("untracked.txt");
            Files.writeString(untrackedFile, "Untracked File", StandardCharsets.UTF_8);
            assertThat(untrackedFile).exists();
            causeNextGitOpenFail(returnValue);

            // When
            UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
            RepoDirAndGit returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

            // Then
            assertThat(returnValue2.getRepoDir()).isEqualTo(returnValue.getRepoDir());
            assertThat(updateOutcome.getNewFile()).exists();
            assertThat(untrackedFile).doesNotExist();
        }
    }

    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenFetchFails() throws IOException, GitAPIException, URISyntaxException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        try (RepoDirAndGit returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString())) {
            // Then
            assertThat(returnValue.getRepoDir()).exists();
            assertThat(returnValue.getRepoDir()).isNotEmptyDirectory();
            Path untrackedFile = returnValue.getRepoDir().resolve("untracked.txt");
            Files.writeString(untrackedFile, "Untracked File", StandardCharsets.UTF_8);
            assertThat(untrackedFile).exists();
            causeNextFetchToFail(returnValue);

            // When
            UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
            RepoDirAndGit returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

            // Then
            assertThat(returnValue2.getRepoDir()).isEqualTo(returnValue.getRepoDir());
            assertThat(updateOutcome.getNewFile()).exists();
            assertThat(untrackedFile).doesNotExist();
        }
    }

    @Test
    public void cloneOrPullRepoShouldCheckoutARefWhenARefIsSpecified() throws IOException, GitAPIException, URISyntaxException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        CreateBranchInRemoteRepoOutcome createBranchOutcome = gitRepoHelper.createBranchInRemoteRepo(createOutcome.getRepoDir());

        // When
        try (RepoDirAndGit returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString(),
                createBranchOutcome.getNewBranchName())) {
            // Then
            assertThat(returnValue.getRepoDir()).exists();
            assertThat(returnValue.getRepoDir()).isNotEmptyDirectory();
            assertThat(returnValue.getGit().getRepository().getBranch()).isEqualTo(createBranchOutcome.getNewFileCommitHash());
        }
    }

    @Test
    public void cloneOrPullRepoShouldThrowAnExceptionWhenARefIsSpecifiedThatDoesNotExist() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        Throwable thrown = catchThrowable(() -> underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString(), "branch_that_does_not_exist"));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessageStartingWith("Could not find ref \"branch_that_does_not_exist\" on remote repo \""
                + createOutcome.getRepoDir().toString() + "\"");
    }

    private void createGitCloner() throws IOException {
        GitConfig gitConfig = new GitConfig(tempDir.resolve("repos").toString(), List.of());
        underTest = new GitCloner(gitConfig);
        underTest.initialize();
    }

    private Instant getLastModifiedTime(RepoDirAndGit repoDirAndGit) throws IOException {
        return Files.getLastModifiedTime(repoDirAndGit.getRepoDir()).toInstant();
    }

    private void causeNextGitOpenFail(RepoDirAndGit repoDirAndGit) throws IOException {
        FileSystemUtils.deleteRecursively(repoDirAndGit.getRepoDir().resolve(".git"));
    }

    private void causeNextFetchToFail(RepoDirAndGit repoDirAndGit) throws GitAPIException {
        repoDirAndGit.getGit().remoteRemove().setRemoteName("origin").call();
    }
}
