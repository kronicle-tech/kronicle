package tech.kronicle.plugins.git;

import com.google.common.io.RecursiveDeleteOption;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.kronicle.plugins.git.config.GitConfig;
import tech.kronicle.plugins.git.testutils.CreateBranchInRemoteRepoOutcome;
import tech.kronicle.plugins.git.testutils.CreateRemoteRepoOutcome;
import tech.kronicle.plugins.git.testutils.GitRepoHelper;
import tech.kronicle.plugins.git.testutils.UpdateRemoteRepoOutcome;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static com.google.common.io.MoreFiles.deleteRecursively;
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
    public void cloneOrPullRepoShouldCloneARepoWhenNotClonedBefore() {
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
    public void cloneOrPullRepoShouldFetchARepoWhenClonedBefore() {
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

    @SneakyThrows
    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenGitOpenFails() {
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

    @SneakyThrows
    @Test
    public void cloneOrPullRepoShouldDeleteAllAndCloneAgainWhenFetchFails() {
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

    @SneakyThrows
    @Test
    public void cloneOrPullRepoShouldCheckoutARefWhenARefIsSpecified() {
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
    public void cloneOrPullRepoShouldThrowAnExceptionWhenARefIsSpecifiedThatDoesNotExist() {
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

    private void createGitCloner() {
        GitConfig config = new GitConfig(tempDir.resolve("repos").toString(), List.of());
        underTest = new GitClonerImpl(config);
    }

    @SneakyThrows
    private Instant getLastModifiedTime(Path repoDir) {
        return Files.getLastModifiedTime(repoDir).toInstant();
    }

    @SneakyThrows
    private void causeNextGitOpenFail(Path repoDir) {
        deleteRecursively(repoDir.resolve(".git"), RecursiveDeleteOption.ALLOW_INSECURE);
    }

    private void causeNextFetchToFail(Path repoDir) throws GitAPIException, IOException {
        try (Git git = Git.open(repoDir.toFile())) {
            git.remoteRemove().setRemoteName("origin").call();
        }
    }
}
