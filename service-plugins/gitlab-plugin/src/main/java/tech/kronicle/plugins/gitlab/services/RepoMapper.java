package tech.kronicle.plugins.gitlab.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.gitlab.GitLabPlugin;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.Link;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.common.CaseUtils.toTitleCase;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class RepoMapper {

    private final GitLabConfig config;

    public Repo mapRepo(EnrichedGitLabRepo repo) {
        return Repo.builder()
                .url(repo.getRepo().getHttp_url_to_repo())
                .defaultBranch(repo.getRepo().getDefault_branch())
                .hasComponentMetadataFile(repo.getHasComponentMetadataFile())
                .build();
    }

    public List<CheckState> mapChecks(List<GitLabJob> jobs, LocalDateTime now) {
        return jobs.stream()
                .filter(this::excludeCertainJobStatuses)
                .map(mapCheck(now))
                .collect(toUnmodifiableList());
    }

    private boolean excludeCertainJobStatuses(GitLabJob gitLabJob) {
        switch (gitLabJob.getStatus()) {
            case "created":
            case "pending":
            case "manual":
            case "canceled":
            case "skipped":
                return false;
            default:
                return true;
        }
    }

    private Function<GitLabJob, CheckState> mapCheck(LocalDateTime now) {
        return job -> CheckState.builder()
                .environmentId(config.getEnvironmentId())
                .pluginId(GitLabPlugin.ID)
                .name(job.getName())
                .description("GitLab Job")
                .status(mapCheckStatus(job))
                .statusMessage(toTitleCase(job.getStatus()))
                .links(createWorkflowRunLinks(job))
                .updateTimestamp(now)
                .build();
    }

    private ComponentStateCheckStatus mapCheckStatus(GitLabJob job) {
        switch (job.getStatus()) {
            case "running":
                return ComponentStateCheckStatus.PENDING;
            case "failed":
                return ComponentStateCheckStatus.CRITICAL;
            case "success":
                return ComponentStateCheckStatus.OK;
            default:
                log.warn("Unrecognised job status \"{}\"", job.getStatus());
                return ComponentStateCheckStatus.UNKNOWN;
        }
    }

    private List<Link> createWorkflowRunLinks(GitLabJob job) {
        if (isNull(job.getWeb_url())) {
            return List.of();
        }

        return List.of(
                Link.builder()
                        .url(job.getWeb_url())
                        .description("GitLab Job")
                        .build()
        );
    }
}
