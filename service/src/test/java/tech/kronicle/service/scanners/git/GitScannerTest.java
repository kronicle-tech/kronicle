package tech.kronicle.service.scanners.git;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.sdk.models.git.Identity;
import tech.kronicle.service.config.GitConfig;
import tech.kronicle.service.mappers.ThrowableToScannerErrorMapper;
import tech.kronicle.service.scanners.BaseScannerTest;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.services.GitCloner;
import tech.kronicle.service.testutils.CreateRemoteRepoOutcome;
import tech.kronicle.service.testutils.GitRepoHelper;
import tech.kronicle.service.testutils.RepoOperationOption;
import tech.kronicle.service.testutils.UpdateRemoteRepoOutcome;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GitScannerTest extends BaseScannerTest {

    @TempDir
    public Path tempDir;
    private GitRepoHelper gitRepoHelper; 
    private GitScanner underTest;

    @BeforeEach
    public void beforeEach() throws IOException {
        gitRepoHelper = new GitRepoHelper(tempDir);
        GitCloner gitCloner = new GitCloner(new GitConfig(tempDir.resolve("repos").toString(), List.of()));
        gitCloner.initialize();
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
    public void scanShouldCloneThenFetchGitRepo() throws IOException, GitAPIException, InterruptedException {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        Repo testRepo = new Repo(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase> returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Codebase codebase = returnValue.getOutput();
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        assertThat(component.getGitRepo()).isNotNull();
        assertThat(component.getGitRepo().getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getLastCommitTimestamp()).isEqualTo(component.getGitRepo().getFirstCommitTimestamp());
        assertThat(component.getGitRepo().getCommitCount()).isEqualTo(1);
        assertThat(component.getGitRepo().getCommitters()).hasSize(1);
        Identity identity;
        identity = component.getGitRepo().getCommitters().get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(1);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isEqualTo(identity.getFirstCommitTimestamp());
        assertThat(component.getGitRepo().getAuthorCount()).isEqualTo(1);
        assertThat(component.getGitRepo().getCommitCount()).isEqualTo(1);

        // When
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir());
        returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        codebase = returnValue.getOutput();
        component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        assertThat(component.getGitRepo()).isNotNull();
        assertThat(component.getGitRepo().getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getCommitCount()).isEqualTo(2);
        assertThat(component.getGitRepo().getCommitters()).hasSize(1);
        identity = component.getGitRepo().getCommitters().get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getAuthorCount()).isEqualTo(1);
        assertThat(component.getGitRepo().getCommitterCount()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideIdentityTypes")
    public void scanFindMultipleIdentitiesSortedInDescendingOrderByCommitCount(IdentityType identityType) throws IOException, GitAPIException, InterruptedException {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityOption(identityType));
        UpdateRemoteRepoOutcome updateOutcome2 = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityOption(identityType));
        Repo testRepo = new Repo(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase> returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Codebase codebase = returnValue.getOutput();
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        assertThat(component.getGitRepo()).isNotNull();
        assertThat(component.getGitRepo().getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getCommitCount()).isEqualTo(3);
        Identity identity;

        List<Identity> mainIdentities = getMainIdentityList(identityType, component);
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

        List<Identity> otherIdentities = getOtherIdentityList(identityType, component);
        assertThat(otherIdentities).hasSize(1);
        identity = otherIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(3);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome2.getBeforeCommit(), updateOutcome2.getAfterCommit());

        assertThat(component.getGitRepo().getAuthorCount()).isEqualTo(component.getGitRepo().getAuthors().size());
        assertThat(component.getGitRepo().getCommitterCount()).isEqualTo(component.getGitRepo().getCommitters().size());
    }

    @ParameterizedTest
    @MethodSource("provideIdentityTypes")
    public void scanFindMultipleNamesForAnIdentity(IdentityType identityType) throws IOException, GitAPIException, InterruptedException {
        // Given
        CreateRemoteRepoOutcome createOutcome = gitRepoHelper.createRemoteRepo();
        waitForNextCommitTimestampToBeForDifferentSecond();
        UpdateRemoteRepoOutcome updateOutcome = gitRepoHelper.updateRemoteGitRepo(createOutcome.getRepoDir(), getDifferentIdentityNameOption(identityType));
        Repo testRepo = new Repo(createOutcome.getRepoDir().toString());

        // When
        Output<Codebase> returnValue = underTest.scan(testRepo);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        Codebase codebase = returnValue.getOutput();
        Component component = getMutatedComponent(returnValue);
        assertThat(codebase).isNotNull();
        assertThat(codebase).isNotNull();
        assertThat(codebase.getRepo()).isEqualTo(testRepo);
        assertThat(codebase.getDir()).isNotEmptyDirectory();
        assertThat(component.getGitRepo()).isNotNull();
        assertThat(component.getGitRepo().getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());
        assertThat(component.getGitRepo().getCommitCount()).isEqualTo(2);
        Identity identity;

        List<Identity> mainIdentities = getMainIdentityList(identityType, component);
        assertThat(mainIdentities).hasSize(1);
        identity = mainIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Alternate Test Person 1", "Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());

        List<Identity> otherIdentities = getOtherIdentityList(identityType, component);
        assertThat(otherIdentities).hasSize(1);
        identity = otherIdentities.get(0);
        assertThat(identity.getNames()).containsExactly("Test Person 1");
        assertThat(identity.getEmailAddress()).isEqualTo("test_person_1@example.com");
        assertThat(identity.getCommitCount()).isEqualTo(2);
        assertThat(identity.getFirstCommitTimestamp()).isBetween(createOutcome.getBeforeCommit(), createOutcome.getAfterCommit());
        assertThat(identity.getLastCommitTimestamp()).isBetween(updateOutcome.getBeforeCommit(), updateOutcome.getAfterCommit());

        assertThat(component.getGitRepo().getAuthorCount()).isEqualTo(component.getGitRepo().getAuthors().size());
        assertThat(component.getGitRepo().getCommitterCount()).isEqualTo(component.getGitRepo().getCommitters().size());
    }

    private static List<IdentityType> provideIdentityTypes() {
        return List.of(IdentityType.values());
    }

    private void waitForNextCommitTimestampToBeForDifferentSecond() throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
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

    private List<Identity> getMainIdentityList(IdentityType identityType, Component component) {
        return identityType == IdentityType.AUTHOR
                ? component.getGitRepo().getAuthors()
                : component.getGitRepo().getCommitters();
    }

    private List<Identity> getOtherIdentityList(IdentityType identityType, Component component) {
        return identityType == IdentityType.AUTHOR
                ? component.getGitRepo().getCommitters()
                : component.getGitRepo().getAuthors();
    }

    private enum IdentityType {

        COMMITTER,
        AUTHOR
    }
}
