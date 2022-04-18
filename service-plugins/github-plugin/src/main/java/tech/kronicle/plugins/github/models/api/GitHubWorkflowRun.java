package tech.kronicle.plugins.github.models.api;

import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@Value
@With
public class GitHubWorkflowRun {

  String name;
  String head_branch;
  String head_sha;
  String status;
  String conclusion;
  Long workflow_id;
  String html_url;
  LocalDateTime created_at;
  LocalDateTime updated_at;
  GitHubWorkflowRunActor actor;
}
