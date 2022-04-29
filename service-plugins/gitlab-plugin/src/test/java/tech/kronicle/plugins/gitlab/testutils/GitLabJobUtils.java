package tech.kronicle.plugins.gitlab.testutils;

import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.models.api.GitLabUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;

public final class GitLabJobUtils {

    public static List<GitLabJob> createGitLabJobs() {
        return List.of(
                createGitLabJob(1),
                createGitLabJob(2)
        );
    }

    public static List<GitLabJob> createGitLabJobs(List<String> statuses) {
        return IntStream.range(0, statuses.size())
                .mapToObj(statusIndex -> createGitLabJob(statusIndex + 1, statuses.get(statusIndex)))
                .collect(toUnmodifiableList());
    }

    public static GitLabJob createGitLabJob(int jobNumber) {
        return createGitLabJob(jobNumber, "test-job-status-1-" + jobNumber);
    }

    public static GitLabJob createGitLabJob(int jobNumber, String status) {
        return new GitLabJob(
                "Test Job Name 1 " + jobNumber,
                status,
                "https://example.com/test-job-web-url-1-" + jobNumber,
                new GitLabUser("https://example.com/test-user-avatar-1-" + jobNumber),
                createTimestamp(jobNumber, 1),
                createTimestamp(jobNumber, 2),
                createTimestamp(jobNumber, 3)
        );
    }

    private static LocalDateTime createTimestamp(int jobNumber, int timestampNumber) {
        return LocalDateTime.of(2001, jobNumber, timestampNumber, 0, 0, 0);
    }

    private GitLabJobUtils() {
    }
}
