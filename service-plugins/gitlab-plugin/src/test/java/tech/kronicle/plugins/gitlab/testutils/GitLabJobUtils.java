package tech.kronicle.plugins.gitlab.testutils;

import tech.kronicle.plugins.gitlab.models.api.GitLabJob;
import tech.kronicle.plugins.gitlab.models.api.GitLabUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;

public final class GitLabJobUtils {

    public static List<GitLabJob> createGitLabJobs() {
        return createGitLabJobs(1);
    }

    public static List<GitLabJob> createGitLabJobs(int repoNumber) {
        return List.of(
                createGitLabJob(repoNumber, 1),
                createGitLabJob(repoNumber, 2)
        );
    }

    public static List<GitLabJob> createGitLabJobs(List<String> statuses) {
        return IntStream.range(0, statuses.size())
                .mapToObj(statusIndex -> createGitLabJob(statusIndex + 1, statuses.get(statusIndex)))
                .collect(toUnmodifiableList());
    }

    public static GitLabJob createGitLabJob(int jobNumber) {
        return createGitLabJob(1, jobNumber);
    }

    public static GitLabJob createGitLabJob(int jobNumber, String status){
        return createGitLabJob(1, jobNumber, status);
    }

    public static GitLabJob createGitLabJob(int repoNumber, int jobNumber) {
        return createGitLabJob(repoNumber, jobNumber, "test-job-status-" + repoNumber + "-" + jobNumber);
    }

    public static GitLabJob createGitLabJob(int repoNumber, int jobNumber, String status) {
        return new GitLabJob(
                "Test Job Name " + repoNumber + " " + jobNumber,
                status,
                "https://example.com/test-job-web-url-" + repoNumber + "-" + jobNumber,
                new GitLabUser("https://example.com/test-user-avatar-" + repoNumber + "-" + jobNumber),
                createTimestamp(repoNumber, jobNumber, 1),
                createTimestamp(repoNumber, jobNumber, 2),
                createTimestamp(repoNumber, jobNumber, 3)
        );
    }

    private static LocalDateTime createTimestamp(int repoNumber, int jobNumber, int timestampNumber) {
        return LocalDateTime.of(2000 + repoNumber, jobNumber, timestampNumber, 0, 0, 0);
    }

    private GitLabJobUtils() {
    }
}
