package tech.kronicle.plugins.github.models.api;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GitHubGetWorkflowRunsResponse {

  List<GitHubWorkflowRun> workflow_runs;
}
