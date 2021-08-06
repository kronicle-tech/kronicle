package com.moneysupermarket.componentcatalog.service.testutils;

import com.google.common.io.BaseEncoding;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class GitRepoHelper {

    public static final Random RANDOM = new Random();

    private final Path tempDir;

    public CreateRemoteRepoOutcome createRemoteRepo() throws IOException, GitAPIException {
        Path repoDir = tempDir.resolve("remote-git-repo");
        assertThat(Files.exists(repoDir)).isFalse();
        Files.createDirectory(repoDir);
        Path readmeFile = repoDir.resolve("README.md");
        Files.writeString(readmeFile, "# Example Git Repo\n");
        LocalDateTime beforeCommit;
        try (Git git = Git.init().setDirectory(repoDir.toFile()).call()) {
            git.add().addFilepattern(".").call();
            beforeCommit = nowWithoutSubSeconds();
            git.commit().setMessage("Initial commit").setAuthor(createPerson1(false)).setCommitter(createPerson1(false)).call();
        }
        LocalDateTime afterCommit = nowWithoutSubSeconds();
        return new CreateRemoteRepoOutcome(repoDir, beforeCommit, afterCommit);
    }

    public UpdateRemoteRepoOutcome updateRemoteGitRepo(Path repoDir) throws IOException, GitAPIException {
        return updateRemoteGitRepo(repoDir, EnumSet.noneOf(RepoOperationOption.class));
    }

    public UpdateRemoteRepoOutcome updateRemoteGitRepo(Path repoDir, RepoOperationOption firstOption, RepoOperationOption... restOptions) throws IOException, GitAPIException {
        return updateRemoteGitRepo(repoDir, EnumSet.of(firstOption, restOptions));
    }

    private UpdateRemoteRepoOutcome updateRemoteGitRepo(Path repoDir, EnumSet<RepoOperationOption> options) throws IOException, GitAPIException {
        Path randomTextFile = repoDir.resolve(generateRandomFileName());
        Files.writeString(randomTextFile, generateRandomText());
        LocalDateTime beforeCommit;
        RevCommit commit;
        try (Git git = Git.open(repoDir.toFile())) {
            git.add().addFilepattern(randomTextFile.getFileName().toString()).call();
            beforeCommit = nowWithoutSubSeconds();
            PersonIdent committer = options.contains(RepoOperationOption.DIFFERENT_COMMITTER)
                    ? createPerson2()
                    : createPerson1(options.contains(RepoOperationOption.DIFFERENT_COMMITTER_NAME));
            commit = git
                    .commit()
                    .setMessage("Add random text file")
                    .setAuthor(options.contains(RepoOperationOption.DIFFERENT_AUTHOR)
                            ? createPerson2()
                            : createPerson1(options.contains(RepoOperationOption.DIFFERENT_AUTHOR_NAME)))
                    .setCommitter(committer)
                    .call();
        }
        LocalDateTime afterCommit = nowWithoutSubSeconds();
        return new UpdateRemoteRepoOutcome(randomTextFile, commit.getName(), beforeCommit, afterCommit);
    }

    public CreateBranchInRemoteRepoOutcome createBranchInRemoteRepo(Path repoDir) throws IOException, GitAPIException {
        String randomBranchName = generateRandomBranchName();
        UpdateRemoteRepoOutcome updateRemoteGitRepoOutcome;
        try (Git git = Git.open(repoDir.toFile())) {
            git.branchCreate().setName(randomBranchName).call();
            git.checkout().setName(randomBranchName).call();
            updateRemoteGitRepoOutcome = updateRemoteGitRepo(repoDir);
        }
        return new CreateBranchInRemoteRepoOutcome(randomBranchName, updateRemoteGitRepoOutcome.getNewFile(),
                updateRemoteGitRepoOutcome.getNewFileCommitHash());
    }

    private PersonIdent createPerson1(boolean differentName) {
        return createPerson(differentName ? "Alternate Test Person 1" : "Test Person 1", "test_person_1@example.com");
    }

    private PersonIdent createPerson2() {
        return createPerson("Test Person 2", "test_person_2@example.com");
    }

    private PersonIdent createPerson(String name, String emailAddress) {
        // BEWARE: PersonIdent includes a timestamp that is used as the commit timestamp. A new PersonIdent object must be created for each commit and not reused
        return new PersonIdent(name, emailAddress);
    }

    private String generateRandomText() {
        return generateRandomText(16);
    }

    private String generateRandomText(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return BaseEncoding.base16().lowerCase().encode(bytes).substring(0, length);
    }

    private String generateRandomFileName() {
        return "file_" + generateRandomText(10) + ".txt";
    }

    private String generateRandomBranchName() {
        return "branch_" + generateRandomText(10);
    }

    private LocalDateTime nowWithoutSubSeconds() {
        return LocalDateTime.now(ZoneOffset.UTC)
                .withNano(0);
    }
}
