package tech.kronicle.plugins.gitlab.testutils;

import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;

import static tech.kronicle.plugins.gitlab.testutils.RepoScenario.NO_DEFAULT_BRANCH;

public final class GitLabRepoUtils {

    public static GitLabRepo createGitLabRepo(int repoListNumber, int repoNumber) {
        long id = (repoListNumber * 100L) + repoNumber;
        return new GitLabRepo(
                id,
                "test-default-branch-" + repoListNumber + "-" + repoNumber,
                "https://example.com/repo-" + repoListNumber + "-" + repoNumber + ".git"
        );
    }

    public static GitLabRepo createGitLabRepo(int repoNumber, RepoScenario repoScenario) {
        return new GitLabRepo(
                (long) repoNumber,
                getDefaultBranch(repoNumber, repoScenario),
                "https://example.com/repo-" + repoNumber + "-" + repoScenario + ".git"
        );
    }

    private static String getDefaultBranch(int repoNumber, RepoScenario repoScenario) {
        return repoScenario == NO_DEFAULT_BRANCH ? null : "branch-" + repoNumber;
    }

    private GitLabRepoUtils() {
    }
}
