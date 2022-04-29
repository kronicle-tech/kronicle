package tech.kronicle.plugins.gitlab.testutils;

import tech.kronicle.plugins.gitlab.config.GitLabAccessTokenConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;

import java.util.List;
import java.util.Optional;

import static tech.kronicle.plugins.gitlab.testutils.GitLabRepoUtils.createGitLabRepo;

public final class EnrichedGitLabRepoUtils {

    public static List<EnrichedGitLabRepo> createEnrichedGitLabRepos(int repoListNumber) {
        return List.of(
                createEnrichedGitLabRepo(repoListNumber, 1),
                createEnrichedGitLabRepo(repoListNumber, 2)
        );
    }

    public static EnrichedGitLabRepo createEnrichedGitLabRepo(int repoListNumber, int repoNumber) {
        GitLabRepo repo = createGitLabRepo(repoListNumber, repoNumber);
        return EnrichedGitLabRepo.builder()
                .repo(repo)
                .accessToken(new GitLabAccessTokenConfig("test-access-token-" + repoListNumber + "-" + repoNumber))
                .baseUrl("https://example.com/test-base-url-" + repoListNumber + "-" + repoNumber)
                .hasComponentMetadataFile(repo.getId() % 2 == 0)
                .build();
    }

    public static EnrichedGitLabRepo createEnrichedGitLabRepo(
            String baseUrl,
            GitLabAccessTokenConfig accessToken,
            int repoNumber,
            RepoScenario repoScenario
    ) {
        GitLabRepo repo = createGitLabRepo(repoNumber, repoScenario);
        return EnrichedGitLabRepo.builder()
                .repo(repo)
                .accessToken(createAccessToken(accessToken))
                .baseUrl(baseUrl)
                .hasComponentMetadataFile(hasComponentMetadataFile(repoScenario))
                .build();
    }

    private static GitLabAccessTokenConfig createAccessToken(GitLabAccessTokenConfig accessToken) {
        return Optional.ofNullable(accessToken)
                .map(GitLabAccessTokenConfig::getValue)
                .map(GitLabAccessTokenConfig::new)
                .orElse(null);
    }

    private static boolean hasComponentMetadataFile(RepoScenario repoScenario) {
        switch (repoScenario) {
            case NO_DEFAULT_BRANCH:
            case NO_KRONICLE_METADATA_FILE:
                return false;
            default:
                return true;
        }
    }

    private EnrichedGitLabRepoUtils() {
    }
}
