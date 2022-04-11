package tech.kronicle.plugins.gitlab.models.api;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GitLabJob {

    String name;
    String status;
    String web_url;
    GitLabUser user;
    LocalDateTime created_at;
    LocalDateTime started_at;
    LocalDateTime finished_at;
}
