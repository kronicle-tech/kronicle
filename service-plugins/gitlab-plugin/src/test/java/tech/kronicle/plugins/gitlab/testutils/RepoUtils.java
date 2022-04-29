package tech.kronicle.plugins.gitlab.testutils;

import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static tech.kronicle.plugins.gitlab.testutils.RepoScenario.NO_DEFAULT_BRANCH;

public final class RepoUtils {

    public static final Clock CLOCK = Clock.fixed(
            LocalDateTime.of(2001, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    public static List<Repo> createRepos() {
        return List.of(
                createRepo(1),
                createRepo(2)
        );
    }

    public static Repo createRepo(int repoNumber) {
        return createRepo(repoNumber, RepoScenario.NORMAL);
    }

    public static Repo createRepo(int repoNumber, RepoScenario repoScenario) {
        return Repo.builder()
                .url("https://example.com/repo-" + repoNumber + "-" + repoScenario + ".git")
                .defaultBranch(getDefaultBranch(repoNumber, repoScenario))
                .hasComponentMetadataFile(getHasComponentMetadataFile(repoScenario))
                .state(createRepoState(repoNumber, repoScenario))
                .build();
    }

    public static ComponentState createRepoState(int repoNumber) {
        return createRepoState(repoNumber, RepoScenario.NORMAL);
    }

    public static ComponentState createRepoState(int repoNumber, RepoScenario repoScenario) {
        if (repoScenario == NO_DEFAULT_BRANCH ||
                repoScenario == RepoScenario.PIPELINES_FORBIDDEN) {
            return null;
        }
        return ComponentState.builder()
                .environments(List.of(
                        EnvironmentState.builder()
                                .id("test-environment-id")
                                .plugins(List.of(
                                        EnvironmentPluginState.builder()
                                                .id("gitlab")
                                                .checks(List.of(
                                                        createCheck(repoNumber, 1),
                                                        createCheck(repoNumber, 2),
                                                        createCheck(repoNumber, 3),
                                                        createCheck(repoNumber, 4),
                                                        createCheck(repoNumber, 5)
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private static String getDefaultBranch(int repoNumber, RepoScenario repoScenario) {
        return repoScenario == NO_DEFAULT_BRANCH ? null : "branch-" + repoNumber;
    }

    private static boolean getHasComponentMetadataFile(RepoScenario repoScenario) {
        switch (repoScenario) {
            case NO_DEFAULT_BRANCH:
            case NO_KRONICLE_METADATA_FILE:
                return false;
            default:
                return true;
        }
    }

    private static CheckState createCheck(int repoNumber, int checkNumber) {
        return CheckState.builder()
                .name("Test name " + repoNumber + " " + checkNumber)
                .description("GitLab Job")
                .status(ComponentStateCheckStatus.OK)
                .statusMessage("Success")
                .links(List.of(
                        Link.builder()
                                .url("https://example.com/web-url-" + repoNumber + "-" + checkNumber)
                                .description("GitLab Job")
                                .build()
                ))
                .updateTimestamp(LocalDateTime.now(CLOCK))
                .build();
    }

    private RepoUtils() {
    }
}
