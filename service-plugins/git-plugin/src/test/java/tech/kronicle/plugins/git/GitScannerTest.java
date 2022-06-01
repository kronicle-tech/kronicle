package tech.kronicle.plugins.git;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.git.config.GitConfig;
import tech.kronicle.plugins.git.testutils.CreateRemoteRepoOutcome;
import tech.kronicle.plugins.git.testutils.GitRepoHelper;
import tech.kronicle.plugins.git.testutils.RepoOperationOption;
import tech.kronicle.plugins.git.testutils.UpdateRemoteRepoOutcome;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.git.GitRepoState;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;
import tech.kronicle.sdk.models.git.Identity;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GitScannerTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    @TempDir
    public Path tempDir;
    private GitRepoHelper gitRepoHelper;
    private GitScanner underTest;

    @BeforeEach
    public void beforeEach() {
        gitRepoHelper = new GitRepoHelper(tempDir);
        GitClonerImpl gitCloner = new GitClonerImpl(new GitConfig(tempDir.resolve("repos").toString(), List.of()));
        underTest = new GitScanner(gitCloner, new ThrowableToScannerErrorMapper(), null);
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("git");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("If a component includes a repo URL, the scanner will create a local clone of the component's Git repo.  The local "
                + "Git repo clones are typically used as an input to other scanners");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldCloneThenFetchGitRepo() {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        RepoReference testRepo = new RepoReference(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase, Component> returnValue = underTest.scan(testRepo);

        // Then
        Codebase codebase = returnValue.getOutput();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        GitRepoState gitRepo = getGitRepo(component);
        assertThat(gitRepo).isNotNull();
        assertThat(gitRepo.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(gitRepo.getLastCommitTimestamp()).isEqualTo(gitRepo.getFirstCommitTimestamp());
        assertThat(gitRepo.getCommitCount()).isEqualTo(1);
        assertThat(gitRepo.getCommitters()).hasSize(1);
        Identity identity;
        identity = gitRepo.getCommitters().get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(1);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isEqualTo(identity.getFirstCommitTimestamp());
        assertThat(gitRepo.getAuthorCount()).isEqualTo(1);
        assertThat(gitRepo.getCommitCount()).isEqualTo(1);

        // When
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
        returnValue = underTest.scan(testRepo);

        // Then
        component = getMutatedComponent(returnValue);
        assertThat(returnValue.getErrors()).isEmpty();
        codebase = returnValue.getOutput();
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        gitRepo = getGitRepo(component);
        assertThat(gitRepo).isNotNull();
        assertThat(gitRepo.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(gitRepo.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(gitRepo.getCommitCount()).isEqualTo(2);
        assertThat(gitRepo.getCommitters()).hasSize(1);
        identity = gitRepo.getCommitters().get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(gitRepo.getAuthorCount()).isEqualTo(1);
        assertThat(gitRepo.getCommitterCount()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideIdentityTypes")
    public void scanFindMultipleIdentitiesSortedInDescendingOrderByCommitCount(IdentityType identityType) {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityOption(identityType));
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome2 = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityOption(identityType));
        RepoReference testRepo = new RepoReference(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase, Component> returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Codebase codebase = returnValue.getOutput();
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        GitRepoState gitRepo = getGitRepo(component);
        assertThat(gitRepo).isNotNull();
        assertThat(gitRepo.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(gitRepo.getLastCommitTimestamp()).isBetween(updateOutcome2.getBeforeCommit(), updateOutcome2.getAfterCommit());
        assertThat(gitRepo.getCommitCount()).isEqualTo(3);
        Identity identity;

        List<Identity> mainIdentities = getMainIdentityList(identityType, gitRepo);
        assertThat(mainIdentities).hasSize(2);
        identity = mainIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 2");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_2@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome2.getBeforeCommit(), updateOutcome2.getAfterCommit());
        identity = mainIdentities.get(1);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(1);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isEqualTo(identity.getFirstCommitTimestamp());

        List<Identity> otherIdentities = getOtherIdentityList(identityType, gitRepo);
        assertThat(otherIdentities).hasSize(1);
        identity = otherIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(3);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome2.getBeforeCommit(), updateOutcome2.getAfterCommit());

        assertThat(gitRepo.getAuthorCount()).isEqualTo(gitRepo.getAuthors().size());
        assertThat(gitRepo.getCommitterCount()).isEqualTo(gitRepo.getCommitters().size());
    }

    @ParameterizedTest
    @MethodSource("provideIdentityTypes")
    public void scanFindMultipleNamesForAnIdentity(IdentityType identityType) {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityNameOption(identityType));
        RepoReference testRepo = new RepoReference(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase, Component> returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Codebase codebase = returnValue.getOutput();
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        GitRepoState gitRepo = getGitRepo(component);
        assertThat(gitRepo).isNotNull();
        assertThat(gitRepo.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(gitRepo.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(gitRepo.getCommitCount()).isEqualTo(2);
        Identity identity;

        List<Identity> mainIdentities = getMainIdentityList(identityType, gitRepo);
        assertThat(mainIdentities).hasSize(1);
        identity = mainIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Alternate Test Person 1", "Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());

        List<Identity> otherIdentities = getOtherIdentityList(identityType, gitRepo);
        assertThat(otherIdentities).hasSize(1);
        identity = otherIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());

        assertThat(gitRepo.getAuthorCount()).isEqualTo(gitRepo.getAuthors().size());
        assertThat(gitRepo.getCommitterCount()).isEqualTo(gitRepo.getCommitters().size());
    }

    private static List<IdentityType> provideIdentityTypes() {
        return List.of(IdentityType.values());
    }

    @SneakyThrows
    private void waitForNextCommitTimestampToBeForDifferentSecond() {
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
    }

    private GitRepoState getGitRepo(Component component) {
        GitRepoState state = component.getState(GitRepoState.TYPE);
        assertThat(state).isNotNull();
        return state;
    }

    private RepoOperationOption getDifferentIdentityOption(IdentityType identityType) {
        return identityType == IdentityType.AUTHOR
                ? RepoOperationOption.DIFFERENT_AUTHOR
                : RepoOperationOption.DIFFERENT_COMMITTER;
    }

    private RepoOperationOption getDifferentIdentityNameOption(IdentityType identityType) {
        return identityType == IdentityType.AUTHOR
                ? RepoOperationOption.DIFFERENT_AUTHOR_NAME
                : RepoOperationOption.DIFFERENT_COMMITTER_NAME;
    }

    private List<Identity> getMainIdentityList(IdentityType identityType, GitRepoState gitRepo) {
        return identityType == IdentityType.AUTHOR
                ? gitRepo.getAuthors()
                : gitRepo.getCommitters();
    }

    private List<Identity> getOtherIdentityList(IdentityType identityType, GitRepoState gitRepo) {
        return identityType == IdentityType.AUTHOR
                ? gitRepo.getCommitters()
                : gitRepo.getAuthors();
    }

    private enum IdentityType {

        COMMITTER,
        AUTHOR
    }
}
