package tech.kronicle.plugins.git;

import org.eclipse.jgit.api.Git;
import tech.kronicle.plugins.git.config.GitConfig;
import tech.kronicle.plugins.git.testutils.CreateBranchInRemoteRepoOutcome;
import tech.kronicle.plugins.git.testutils.CreateRemoteRepoOutcome;
import tech.kronicle.plugins.git.testutils.GitRepoHelper;
import tech.kronicle.plugins.git.testutils.UpdateRemoteRepoOutcome;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GitClonerImplTest {

    @TempDir
    public Path tempDir;
    private GitRepoHelper gitRepoHelper;
    private GitClonerImpl underTest;

    @BeforeEach
    public void beforeEach() {
        gitRepoHelper = new GitRepoHelper(tempDir);
    }

    @Test
    public void cloneOrPullRepoShouldCloneARepoWhenNotClonedBefore() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        Path returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());
        
        // Then
        assertThat(returnValue).exists();
        assertThat(returnValue).isNotEmptyDirectory();
    }

    @Test
    public void cloneOrPullRepoShouldFetchARepoWhenClonedBefore() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        Path returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue).exists();
        assertThat(returnValue).isNotEmptyDirectory();
        Instant lastModifiedTime = getLastModifiedTime(returnValue);

        // When
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
        Path returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue2).isEqualTo(returnValue);
        Instant lastModifiedTime2 = getLastModifiedTime(returnValue);
        assertThat(lastModifiedTime2).isAfter(lastModifiedTime);
        assertThat(updateOutcome.getNewFile()).exists();
    }

    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenGitOpenFails() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        Path returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue).exists();
        assertThat(returnValue).isNotEmptyDirectory();
        Path untrackedFile = returnValue.resolve("untracked.txt");
        Files.writeString(untrackedFile, "Untracked File", StandardCharsets.UTF_8);
        assertThat(untrackedFile).exists();
        causeNextGitOpenFail(returnValue);

        // When
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
        Path returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue2).isEqualTo(returnValue);
        assertThat(updateOutcome.getNewFile()).exists();
        assertThat(untrackedFile).doesNotExist();
    }

    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenFetchFails() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();

        // When
        Path returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue).exists();
        assertThat(returnValue).isNotEmptyDirectory();
        Path untrackedFile = returnValue.resolve("untracked.txt");
        Files.writeString(untrackedFile, "Untracked File", StandardCharsets.UTF_8);
        assertThat(untrackedFile).exists();
        causeNextFetchToFail(returnValue);

        // When
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
        Path returnValue2 = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString());

        // Then
        assertThat(returnValue2).isEqualTo(returnValue);
        assertThat(updateOutcome.getNewFile()).exists();
        assertThat(untrackedFile).doesNotExist();
    }

    @Test
    public void cloneOrPullRepoShouldCheckoutARefWhenARefIsSpecified() throws IOException, GitAPIException {
        // Given
        createGitCloner();
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        CreateBranchInRemoteRepoOutcome createBranchOutcome = gitRepoHelper.createBranchInRemoteRepo(createOutcome.getRepoDir());

        // When
        Path returnValue = underTest.cloneOrPullRepo(createOutcome.getRepoDir().toString(), createBranchOutcome.getNewBranchName());

        // Then
        assertThat(returnValue).exists();
        assertThat(returnValue).isNotEmptyDirectory();

        try (Git git = Git.open(returnValue.toFile())) {
            assertThat(git.getRepository().getBranch()).isEqualTo(createBranchOutcome.getNewFileCommitHash());
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
        underTest = new GitClonerImpl(gitConfig);
        underTest.initialize();
    }

    private Instant getLastModifiedTime(Path repoDir) throws IOException {
        return Files.getLastModifiedTime(repoDir).toInstant();
    }

    private void causeNextGitOpenFail(Path repoDir) throws IOException {
        FileSystemUtils.deleteRecursively(repoDir.resolve(".git"));
    }

    private void causeNextFetchToFail(Path repoDir) throws GitAPIException, IOException {
        try (Git git = Git.open(repoDir.toFile())) {
            git.remoteRemove().setRemoteName("origin").call();
        }
    }
}
