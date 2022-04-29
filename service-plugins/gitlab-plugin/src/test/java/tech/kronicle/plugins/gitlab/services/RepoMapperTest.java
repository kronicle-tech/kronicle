package tech.kronicle.plugins.gitlab.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.models.api.GitLabRepo;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.gitlab.testutils.GitLabJobUtils.createGitLabJobs;

public class RepoMapperTest {

    @Test
    public void mapRepoShouldMapARepo() {
        // Given
        EnrichedGitLabRepo repo = EnrichedGitLabRepo.builder()
                .repo(new GitLabRepo(null, "test-default-branch", "https://example.com/repo.git"))
                .hasComponentMetadataFile(true)
                .build();
        RepoMapper underTest = createUnderTest();

        // When
        Repo returnValue = underTest.mapRepo(repo);

        // Then
        assertThat(returnValue).isEqualTo(
                Repo.builder()
                        .url("https://example.com/repo.git")
                        .defaultBranch("test-default-branch")
                        .hasComponentMetadataFile(true)
                        .build()
        );
    }

    @Test
    public void mapStateShouldMapStateForARepo() {
        // Given
        List<GitLabJob> jobs = createGitLabJobs();
        LocalDateTime now = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
        RepoMapper underTest = createUnderTest();

        // When
        ComponentState returnValue = underTest.mapState(jobs, now);

        // Then
        assertThat(returnValue).isEqualTo(
                createComponentState(List.of(
                        createCheckState(now, 1, ComponentStateCheckStatus.UNKNOWN),
                        createCheckState(now, 2, ComponentStateCheckStatus.UNKNOWN)
                ))
        );
    }

    @Test
    public void mapStateShouldExcludeSomeJobStatusesAndMapOtherJobStatuses() {
        // Given
        List<GitLabJob> jobs = createGitLabJobs(List.of(
            "created",
            "pending",
            "manual",
            "canceled",
            "skipped",
            "running",
            "failed",
            "success",
            "does-not-exist"
        ));
        LocalDateTime now = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
        RepoMapper underTest = createUnderTest();

        // When
        ComponentState returnValue = underTest.mapState(jobs, now);

        // Then
        assertThat(returnValue).isEqualTo(
                createComponentState(List.of(
                        createCheckState(now, 6, ComponentStateCheckStatus.PENDING, "Running"),
                        createCheckState(now, 7, ComponentStateCheckStatus.CRITICAL, "Failed"),
                        createCheckState(now, 8, ComponentStateCheckStatus.OK, "Success"),
                        createCheckState(now, 9, ComponentStateCheckStatus.UNKNOWN, "Does Not Exist")
                ))
        );
    }

    private ComponentState createComponentState(List<CheckState> checks) {
        return ComponentState.builder()
                .environments(List.of(
                        EnvironmentState.builder()
                                .id("test-environment-id")
                                .plugins(List.of(
                                        EnvironmentPluginState.builder()
                                                .id("gitlab")
                                                .checks(checks)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private CheckState createCheckState(LocalDateTime now, int jobNumber, ComponentStateCheckStatus status) {
        return createCheckState(now, jobNumber, status, "Test Job Status 1 " + jobNumber);
    }

    private CheckState createCheckState(
            LocalDateTime now,
            int jobNumber,
            ComponentStateCheckStatus status,
            String statusMessage
    ) {
        return CheckState.builder()
                .name("Test Job Name 1 " + jobNumber)
                .description("GitLab Job")
                .status(status)
                .statusMessage(statusMessage)
                .links(List.of(
                        Link.builder()
                                .url("https://example.com/test-job-web-url-1-" + jobNumber)
                                .description("GitLab Job")
                                .build()
                ))
                .updateTimestamp(now)
                .build();
    }

    private RepoMapper createUnderTest() {
        return new RepoMapper(new GitLabConfig(
                null,
                null,
                "test-environment-id",
                null,
                null
        ));
    }
}
