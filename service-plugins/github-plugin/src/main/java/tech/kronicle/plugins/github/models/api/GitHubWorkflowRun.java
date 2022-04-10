package tech.kronicle.plugins.github.models.api;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GitHubWorkflowRun {

  String name;
  String head_sha;
  String status;
  String conclusion;
  String workflow_id;
  String html_url;
  LocalDateTime created_at;
  LocalDateTime updated_at;
  GitHubWorkflowRunActor actor;
}
